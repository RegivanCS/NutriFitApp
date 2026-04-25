"""
NutriFit - Motor de Cálculos Nutricionais
Engenharia de emagrecimento saudável com Python
"""

import json
import math
from typing import Dict, List, Optional, Tuple


class NutritionEngine:
    """Motor principal de cálculos nutricionais."""

    # Fatores de atividade física
    FATORES_ATIVIDADE = {
        'sedentario': 1.2,
        'levemente_ativo': 1.375,
        'moderadamente_ativo': 1.55,
        'muito_ativo': 1.725,
        'extremamente_ativo': 1.9
    }

    # Classificação IMC
    CLASSIFICACAO_IMC = [
        (0, 18.5, 'Abaixo do peso'),
        (18.5, 24.9, 'Peso normal'),
        (25, 29.9, 'Sobrepeso'),
        (30, 34.9, 'Obesidade grau I'),
        (35, 39.9, 'Obesidade grau II'),
        (40, float('inf'), 'Obesidade grau III')
    ]

    @staticmethod
    def calcular_imc(peso_kg: float, altura_cm: float) -> Dict:
        """
        Calcula o IMC e retorna classificação completa.
        
        Args:
            peso_kg: Peso em quilogramas
            altura_cm: Altura em centímetros
        
        Returns:
            Dict com IMC, classificação, peso ideal min/max
        """
        altura_m = altura_cm / 100
        imc = peso_kg / (altura_m ** 2)
        
        # Classificação
        classificacao = 'Obesidade grau III'
        for min_val, max_val, label in NutritionEngine.CLASSIFICACAO_IMC:
            if min_val <= imc < max_val:
                classificacao = label
                break
        
        # Peso ideal (IMC 18.5 a 24.9)
        peso_ideal_min = 18.5 * (altura_m ** 2)
        peso_ideal_max = 24.9 * (altura_m ** 2)
        
        # Peso a perder para chegar no IMC ideal
        peso_a_perder = max(0, peso_kg - peso_ideal_max)
        
        return {
            'imc': round(imc, 1),
            'classificacao': classificacao,
            'peso_ideal_min': round(peso_ideal_min, 1),
            'peso_ideal_max': round(peso_ideal_max, 1),
            'peso_a_perder': round(peso_a_perder, 1),
            'nivel_risco': 'alto' if imc >= 30 else 'moderado' if imc >= 25 else 'baixo'
        }

    @staticmethod
    def calcular_tmb(peso_kg: float, altura_cm: float, idade: int, sexo: str) -> float:
        """
        Calcula a Taxa Metabólica Basal (TMB).
        Usa a equação de Mifflin-St Jeor (mais precisa).
        
        Args:
            peso_kg: Peso em kg
            altura_cm: Altura em cm
            idade: Idade em anos
            sexo: 'M' para masculino, 'F' para feminino
        
        Returns:
            TMB em kcal/dia
        """
        if sexo.upper() == 'M':
            return (10 * peso_kg) + (6.25 * altura_cm) - (5 * idade) + 5
        else:
            return (10 * peso_kg) + (6.25 * altura_cm) - (5 * idade) - 161

    @staticmethod
    def calcular_metas_diarias(
        peso_kg: float,
        altura_cm: float,
        idade: int,
        sexo: str,
        nivel_atividade: str,
        objetivo: str = 'emagrecer',
        peso_meta: Optional[float] = None
    ) -> Dict:
        """
        Calcula metas diárias completas de nutrientes.
        
        Args:
            peso_kg: Peso atual em kg
            altura_cm: Altura em cm
            idade: Idade em anos
            sexo: 'M' ou 'F'
            nivel_atividade: Nível de atividade física
            objetivo: 'emagrecer', 'manter', 'ganhar_massa'
            peso_meta: Peso desejado (opcional)
        
        Returns:
            Dict com todas as metas nutricionais
        """
        # TMB
        tmb = NutritionEngine.calcular_tmb(peso_kg, altura_cm, idade, sexo)
        
        # Gasto energético total
        fator = NutritionEngine.FATORES_ATIVIDADE.get(nivel_atividade, 1.2)
        get = tmb * fator
        
        # Ajuste conforme objetivo
        if objetivo == 'emagrecer':
            # Déficit de 500-800 kcal para perder 0.5-0.8kg/semana
            calorias_meta = get - 500
            if calorias_meta < 1200:
                calorias_meta = 1200  # Mínimo seguro
        elif objetivo == 'ganhar_massa':
            calorias_meta = get + 300
        else:  # manter
            calorias_meta = get
        
        # Distribuição de macronutrientes (40% carbs, 30% prot, 30% gord)
        # Ajustado para emagrecimento: 35% carbs, 40% prot, 25% gord
        if objetivo == 'emagrecer':
            pct_carbs, pct_prot, pct_gord = 0.35, 0.40, 0.25
        elif objetivo == 'ganhar_massa':
            pct_carbs, pct_prot, pct_gord = 0.45, 0.30, 0.25
        else:
            pct_carbs, pct_prot, pct_gord = 0.45, 0.25, 0.30
        
        # Cálculo em gramas
        # Carbs: 4 kcal/g, Proteínas: 4 kcal/g, Gorduras: 9 kcal/g
        proteinas_g = round((calorias_meta * pct_prot) / 4, 1)
        carboidratos_g = round((calorias_meta * pct_carbs) / 4, 1)
        gorduras_g = round((calorias_meta * pct_gord) / 9, 1)
        
        # Fibras (25-30g para mulheres, 30-38g para homens)
        fibras_min = 25 if sexo.upper() == 'F' else 30
        fibras_max = 30 if sexo.upper() == 'F' else 38
        
        # Água (35ml por kg de peso)
        agua_ml = peso_kg * 35
        
        # Projeção de tempo para atingir meta
        if peso_meta and objetivo == 'emagrecer':
            peso_perder = peso_kg - peso_meta
            # 0.5kg por semana é saudável
            semanas_estimadas = peso_perder / 0.5
        else:
            semanas_estimadas = None
        
        return {
            'tmb': round(tmb, 1),
            'gasto_energetico_total': round(get, 1),
            'calorias_meta': round(calorias_meta, 1),
            'macronutrientes': {
                'proteinas': {'gramas': proteinas_g, 'calorias': round(proteinas_g * 4, 1), 'porcentagem': pct_prot * 100},
                'carboidratos': {'gramas': carboidratos_g, 'calorias': round(carboidratos_g * 4, 1), 'porcentagem': pct_carbs * 100},
                'gorduras': {'gramas': gorduras_g, 'calorias': round(gorduras_g * 9, 1), 'porcentagem': pct_gord * 100}
            },
            'fibras': {'min': fibras_min, 'max': fibras_max},
            'agua_ml': round(agua_ml, 0),
            'semanas_estimadas_meta': round(semanas_estimadas, 1) if semanas_estimadas else None,
            'objetivo': objetivo,
            'deficit_calorico': round(get - calorias_meta, 1)
        }

    @staticmethod
    def calcular_refeicoes_diarias(metas: Dict, refeicoes_por_dia: int = 5) -> List[Dict]:
        """
        Distribui as calorias ao longo das refeições do dia.
        
        Args:
            metas: Dict retornado por calcular_metas_diarias
            refeicoes_por_dia: Número de refeições (3-6)
        
        Returns:
            Lista de refeições com calorias e macronutrientes
        """
        calorias_total = metas['calorias_meta']
        proteinas_total = metas['macronutrientes']['proteinas']['gramas']
        carbs_total = metas['macronutrientes']['carboidratos']['gramas']
        gorduras_total = metas['macronutrientes']['gorduras']['gramas']
        
        # Distribuição padrão (café, lanche, almoço, lanche, jantar)
        if refeicoes_por_dia == 3:
            percentuais = [0.25, 0.40, 0.35]
            nomes = ['Café da Manhã', 'Almoço', 'Jantar']
        elif refeicoes_por_dia == 4:
            percentuais = [0.20, 0.35, 0.10, 0.35]
            nomes = ['Café da Manhã', 'Almoço', 'Lanche Tarde', 'Jantar']
        else:  # 5 refeições
            percentuais = [0.20, 0.10, 0.30, 0.10, 0.30]
            nomes = ['Café da Manhã', 'Lanche Manhã', 'Almoço', 'Lanche Tarde', 'Jantar']
        
        refeicoes = []
        for i, (nome, pct) in enumerate(zip(nomes, percentuais)):
            refeicoes.append({
                'nome': nome,
                'ordem': i + 1,
                'calorias': round(calorias_total * pct, 1),
                'proteinas_g': round(proteinas_total * pct, 1),
                'carboidratos_g': round(carbs_total * pct, 1),
                'gorduras_g': round(gorduras_total * pct, 1),
                'porcentagem': round(pct * 100, 1)
            })
        
        return refeicoes

    @staticmethod
    def calcular_progresso_semanal(
        peso_atual: float,
        peso_inicial: float,
        peso_meta: float,
        calorias_consumidas_media: float,
        calorias_meta: float
    ) -> Dict:
        """
        Analisa o progresso semanal do usuário.
        
        Returns:
            Dict com análise de progresso
        """
        peso_perdido = peso_inicial - peso_atual
        peso_restante = peso_atual - peso_meta
        
        # Percentual de conclusão da meta
        if peso_inicial > peso_meta:
            progresso = (peso_perdido / (peso_inicial - peso_meta)) * 100
        else:
            progresso = 0
        
        # Média de calorias consumidas
        aderencia_calorica = (1 - abs(calorias_consumidas_media - calorias_meta) / calorias_meta) * 100
        
        # Ritmo semanal (kg/semana)
        # Assumindo que está monitorando há pelo menos 1 semana
        ritmo_semanal = peso_perdido  # simplificado
        
        # Previsão de quando atingirá a meta
        if ritmo_semanal > 0:
            semanas_restantes = peso_restante / ritmo_semanal
        else:
            semanas_restantes = None
        
        # Classificação do ritmo
        if ritmo_semanal < 0.3:
            ritmo_classificacao = 'lento'
        elif ritmo_semanal <= 0.8:
            ritmo_classificacao = 'saudavel'
        else:
            ritmo_classificacao = 'acelerado'
        
        return {
            'peso_perdido_kg': round(peso_perdido, 1),
            'peso_restante_kg': round(peso_restante, 1),
            'progresso_percentual': round(max(0, min(100, progresso)), 1),
            'aderencia_calorica': round(aderencia_calorica, 1),
            'ritmo_semanal_kg': round(ritmo_semanal, 2),
            'ritmo_classificacao': ritmo_classificacao,
            'semanas_restantes_estimadas': round(semanas_restantes, 1) if semanas_restantes else None,
            'mensagem_motivacional': NutritionEngine._gerar_mensagem_motivacional(progresso, aderencia_calorica)
        }

    @staticmethod
    def _gerar_mensagem_motivacional(progresso: float, aderencia: float) -> str:
        """Gera mensagem motivacional baseada no progresso."""
        if progresso >= 75:
            return "🌟 Incrível! Você está quase lá! Continue assim!"
        elif progresso >= 50:
            return "💪 Ótimo progresso! Metade do caminho já foi percorrido!"
        elif progresso >= 25:
            return "🔥 Mandou bem! O progresso é consistente, continue!"
        elif progresso > 0:
            return "🌱 Todo começo é difícil, mas você já está no caminho certo!"
        else:
            if aderencia >= 80:
                return "📊 Boa adesão! Os resultados virão em breve!"
            else:
                return "🎯 Foco na meta! Pequenos ajustes fazem grande diferença!"

    @staticmethod
    def calcular_agua_ideal(peso_kg: float, atividade_fisica: bool = False, temperatura_alta: bool = False) -> Dict:
        """Calcula a quantidade ideal de água por dia."""
        base = peso_kg * 35  # ml
        
        if atividade_fisica:
            base += 500  # ml extras para atividade física
        
        if temperatura_alta:
            base += 500  # ml extras para calor
        
        # Distribuição ao longo do dia
            copos_por_vez = 200  # ml por copo
            copos_total = base / copos_por_vez
        
        return {
            'agua_ml': round(base),
            'agua_litros': round(base / 1000, 1),
            'copos_necessarios': round(copos_total),
            'ml_por_copo': copos_por_vez
        }


# Funções de interface para o Kotlin (serão chamadas via Chaquopy)

def calculate_all(peso, altura, idade, sexo, atividade, objetivo, peso_meta=None):
    """
    Função principal que calcula tudo de uma vez.
    Chamada pelo Kotlin via PythonBridge.
    """
    engine = NutritionEngine()
    
    imc_data = engine.calcular_imc(peso, altura)
    metas = engine.calcular_metas_diarias(peso, altura, idade, sexo, atividade, objetivo, peso_meta)
    refeicoes = engine.calcular_refeicoes_diarias(metas)
    agua = engine.calcular_agua_ideal(peso)
    
    return json.dumps({
        'success': True,
        'imc': imc_data,
        'metas': metas,
        'refeicoes': refeicoes,
        'agua': agua
    })

def calculate_progress(peso_atual, peso_inicial, peso_meta, calorias_media, calorias_meta):
    """Calcula o progresso do usuário."""
    engine = NutritionEngine()
    progresso = engine.calcular_progresso_semanal(
        peso_atual, peso_inicial, peso_meta, calorias_media, calorias_meta
    )
    return json.dumps({'success': True, 'progresso': progresso})

def get_nutritional_info(alimento_nome, quantidade_g=100):
    """
    Retorna informações nutricionais de um alimento.
    Placeholder - em produção, consultaria uma base de dados.
    """
    # Base de dados simplificada de alimentos comuns
    database = {
        'arroz branco': {'calorias': 130, 'proteinas': 2.7, 'carboidratos': 28.2, 'gorduras': 0.3, 'fibras': 0.4},
        'feijão preto': {'calorias': 77, 'proteinas': 4.5, 'carboidratos': 14, 'gorduras': 0.5, 'fibras': 8.5},
        'frango grelhado': {'calorias': 165, 'proteinas': 31, 'carboidratos': 0, 'gorduras': 3.6, 'fibras': 0},
        'batata doce': {'calorias': 86, 'proteinas': 1.6, 'carboidratos': 20.1, 'gorduras': 0.1, 'fibras': 3},
        'ovo cozido': {'calorias': 155, 'proteinas': 12.6, 'carboidratos': 1.1, 'gorduras': 11.5, 'fibras': 0},
        'banana': {'calorias': 89, 'proteinas': 1.1, 'carboidratos': 22.8, 'gorduras': 0.3, 'fibras': 2.6},
        'maçã': {'calorias': 52, 'proteinas': 0.3, 'carboidratos': 14, 'gorduras': 0.2, 'fibras': 2.4},
        'brócolis': {'calorias': 34, 'proteinas': 2.8, 'carboidratos': 7, 'gorduras': 0.4, 'fibras': 3.3},
        'salmão': {'calorias': 208, 'proteinas': 20.4, 'carboidratos': 0, 'gorduras': 13.6, 'fibras': 0},
        'abacate': {'calorias': 160, 'proteinas': 2, 'carboidratos': 8.5, 'gorduras': 14.7, 'fibras': 6.7},
    }
    
    alimento = database.get(alimento_nome.lower(), None)
    
    if alimento:
        fator = quantidade_g / 100
        resultado = {k: round(v * fator, 1) for k, v in alimento.items()}
        resultado['alimento'] = alimento_nome
        resultado['quantidade_g'] = quantidade_g
        return json.dumps({'success': True, 'dados': resultado})
    else:
        return json.dumps({'success': False, 'error': 'Alimento não encontrado na base de dados'})
