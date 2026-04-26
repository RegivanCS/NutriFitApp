import os
from flask import Flask, jsonify, request, send_from_directory
from flask_cors import CORS
from datetime import datetime, timedelta
import json
import math

app = Flask(__name__, static_folder="static")
CORS(app)

# ==================== MOTOR DE CÁLCULOS NUTRICIONAIS ====================

FATORES_ATIVIDADE = {
    "sedentario": 1.2,
    "levemente_ativo": 1.375,
    "moderadamente_ativo": 1.55,
    "muito_ativo": 1.725,
    "extremamente_ativo": 1.9
}

def calcular_tmb(peso_kg, altura_cm, idade, sexo):
    if sexo.upper() == "M":
        return (10 * peso_kg) + (6.25 * altura_cm) - (5 * idade) + 5
    else:
        return (10 * peso_kg) + (6.25 * altura_cm) - (5 * idade) - 161

def calcular_metas(peso_kg, altura_cm, idade, sexo, nivel_atividade, objetivo="emagrecer", peso_meta=None):
    tmb = calcular_tmb(peso_kg, altura_cm, idade, sexo)
    fator = FATORES_ATIVIDADE.get(nivel_atividade, 1.2)
    get = tmb * fator

    if objetivo == "emagrecer":
        calorias_meta = max(1200, get - 500)
    elif objetivo == "ganhar_massa":
        calorias_meta = get + 300
    else:
        calorias_meta = get

    if objetivo == "emagrecer":
        pct_carbs, pct_prot, pct_gord = 0.35, 0.40, 0.25
    elif objetivo == "ganhar_massa":
        pct_carbs, pct_prot, pct_gord = 0.45, 0.30, 0.25
    else:
        pct_carbs, pct_prot, pct_gord = 0.45, 0.25, 0.30

    return {
        "calorias_meta": round(calorias_meta, 1),
        "tmb": round(tmb, 1),
        "get": round(get, 1),
        "proteinas_g": round((calorias_meta * pct_prot) / 4, 1),
        "carboidratos_g": round((calorias_meta * pct_carbs) / 4, 1),
        "gorduras_g": round((calorias_meta * pct_gord) / 9, 1),
        "agua_ml": round(peso_kg * 35, 0),
        "objetivo": objetivo
    }

# ==================== RECOMENDAÇÕES ====================

RECOMENDACOES = {
    "cafe_da_manha": {
        "emagrecer": {
            "nome": "Omelete de Claras com Aveia e Frutas",
            "descricao": "2 claras + 1 ovo inteiro, 3 colheres de aveia, 1 banana e café sem açúcar",
            "explicacao": "As claras fornecem proteína de alto valor biológico com poucas calorias. A aveia tem fibras solúveis que aumentam a saciedade. O café acelera o metabolismo.",
            "calorias": 280, "proteinas": 28, "carboidratos": 35, "gorduras": 8, "fibras": 6
        },
        "ganhar_massa": {
            "nome": "Panqueca Proteica com Banana e Pasta de Amendoim",
            "descricao": "3 ovos, 4 colheres de aveia, 1 scoop de whey, 1 banana, 1 colher de pasta de amendoim",
            "explicacao": "45g de proteína para estímulo máximo de síntese muscular. Gorduras boas para superávit calórico. Carboidratos complexos para energia.",
            "calorias": 520, "proteinas": 45, "carboidratos": 48, "gorduras": 18, "fibras": 4
        },
        "manter": {
            "nome": "Ovos Mexidos com Torrada Integral e Abacate",
            "descricao": "2 ovos mexidos, 2 fatias de pão integral, 1/2 abacate, café ou chá",
            "explicacao": "Equilíbrio perfeito de macronutrientes. Proteína completa com aminoácidos essenciais. Gordura monoinsaturada para saciedade prolongada.",
            "calorias": 380, "proteinas": 24, "carboidratos": 30, "gorduras": 18, "fibras": 5
        }
    },
    "almoco": {
        "emagrecer": {
            "nome": "Frango Grelhado com Quinoa e Brócolis",
            "descricao": "200g filé de frango, 1/2 xícara quinoa, brócolis refogado, cenoura",
            "explicacao": "Alto teor proteico aumenta a termogênese. Quinoa tem proteína completa e baixo índice glicêmico. Brócolis é rico em fibras e cálcio.",
            "calorias": 400, "proteinas": 38, "carboidratos": 28, "gorduras": 10, "fibras": 8
        },
        "ganhar_massa": {
            "nome": "Salmão com Batata Doce e Abacate",
            "descricao": "200g filé de salmão, 1 batata doce grande, 1/2 abacate, salada",
            "explicacao": "Salmão rico em ômega-3 reduz inflamação pós-treino. Batata doce repõe glicogênio rápido. Abacate fornece calorias de qualidade.",
            "calorias": 650, "proteinas": 42, "carboidratos": 55, "gorduras": 28, "fibras": 6
        },
        "manter": {
            "nome": "Bife Grelhado com Arroz Integral e Legumes",
            "descricao": "150g carne magra, 4 colheres arroz integral, legumes salteados, salada",
            "explicacao": "Proteína magra para manutenção muscular. Carboidrato complexo de liberação lenta. Vitaminas e minerais dos legumes.",
            "calorias": 500, "proteinas": 33, "carboidratos": 42, "gorduras": 15, "fibras": 7
        }
    },
    "jantar": {
        "emagrecer": {
            "nome": "Salada Completa com Atum e Ovo",
            "descricao": "Mix de folhas, 1 lata de atum, 1 ovo cozido, tomate, cenoura, azeite",
            "explicacao": "Refeição leve e de alta saciedade. Atum rico em ômega-3 anti-inflamatório. Dormir de estômago leve melhora qualidade do sono.",
            "calorias": 300, "proteinas": 28, "carboidratos": 10, "gorduras": 14, "fibras": 6
        },
        "ganhar_massa": {
            "nome": "Macarrão Integral com Carne Moída",
            "descricao": "200g macarrão integral, 150g carne moída magra, molho de tomate caseiro",
            "explicacao": "Carboidrato em abundância repõe glicogênio muscular. Carne fornece ferro e zinco para produção hormonal.",
            "calorias": 600, "proteinas": 38, "carboidratos": 65, "gorduras": 16, "fibras": 5
        },
        "manter": {
            "nome": "Sopa de Legumes com Frango",
            "descricao": "Sopa de abóbora, cenoura, batata doce, 150g frango desfiado, gengibre",
            "explicacao": "Alta hidratação e baixa densidade calórica. Gengibre é termogênico natural. Sopa quente ajuda a relaxar antes de dormir.",
            "calorias": 380, "proteinas": 32, "carboidratos": 30, "gorduras": 12, "fibras": 7
        }
    }
}

# ==================== ROTAS DA API ====================

@app.route("/")
def index():
    return send_from_directory("static", "index.html")

@app.route("/api/calcular", methods=["POST"])
def api_calcular():
    dados = request.json
    metas = calcular_metas(
        dados["peso"], dados["altura"], dados["idade"],
        dados["sexo"], dados["atividade"], dados["objetivo"],
        dados.get("peso_meta")
    )
    return jsonify({"success": True, "metas": metas})

@app.route("/api/recomendar", methods=["POST"])
def api_recomendar():
    dados = request.json
    tipo = dados.get("tipo", "cafe_da_manha")
    objetivo = dados.get("objetivo", "emagrecer")

    if tipo in RECOMENDACOES and objetivo in RECOMENDACOES[tipo]:
        return jsonify({"success": True, "recomendacao": RECOMENDACOES[tipo][objetivo]})
    return jsonify({"success": False, "error": "Tipo ou objetivo não encontrado"}), 404

@app.route("/api/sugestoes", methods=["POST"])
def api_sugestoes():
    dados = request.json
    objetivo = dados.get("objetivo", "emagrecer")
    resultado = {}
    for tipo, refeicoes in RECOMENDACOES.items():
        if objetivo in refeicoes:
            resultado[tipo] = refeicoes[objetivo]
    return jsonify({"success": True, "sugestoes": resultado})

@app.route("/api/explicar", methods=["POST"])
def api_explicar():
    dados = request.json
    nutriente = dados.get("nutriente", "").lower()
    explicacoes = {
        "proteinas": "Essenciais para construir e reparar músculos. Ajudam na saciedade e evitam perda muscular. Fontes: frango, ovos, peixe, leguminosas.",
        "carboidratos": "Principal fonte de energia. Os complexos (integrais) liberam energia aos poucos. Fontes: arroz integral, batata doce, aveia.",
        "gorduras": "Essenciais para absorção de vitaminas e produção hormonal. Gorduras insaturadas são benéficas para o coração.",
        "fibras": "Regulam o intestino e aumentam saciedade. Ajudam a controlar açúcar no sangue. Fontes: verduras, aveia, chia.",
        "calorias": "Unidade de energia. Para emagrecer: consuma menos do que gasta. Para ganhar massa: consuma mais do que gasta."
    }
    return jsonify({"explicacao": explicacoes.get(nutriente, "Nutriente essencial para o bom funcionamento do organismo.")})

@app.route("/api/progresso", methods=["POST"])
def api_progresso():
    dados = request.json
    peso_atual = dados["peso_atual"]
    peso_inicial = dados["peso_inicial"]
    peso_meta = dados["peso_meta"]
    calorias_media = dados.get("calorias_media", 0)
    calorias_meta = dados.get("calorias_meta", 2000)

    peso_perdido = peso_inicial - peso_atual
    peso_restante = peso_atual - peso_meta
    progresso = (peso_perdido / (peso_inicial - peso_meta)) * 100 if peso_inicial > peso_meta else 0
    aderencia = (1 - abs(calorias_media - calorias_meta) / calorias_meta) * 100

    return jsonify({
        "peso_perdido_kg": round(peso_perdido, 1),
        "peso_restante_kg": round(peso_restante, 1),
        "progresso_percentual": round(max(0, min(100, progresso)), 1),
        "aderencia_calorica": round(aderencia, 1)
    })

# Health check
@app.route("/api/health")
def health():
    return jsonify({"status": "ok", "app": "Vitaal", "version": "1.0.0"})

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port, debug=True)