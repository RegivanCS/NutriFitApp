"""
NutriFit - Sugestor Inteligente de Refeições
Recomenda refeições baseadas nas metas calóricas e preferências do usuário
"""

import json
import random
from typing import Dict, List, Optional


class MealSuggester:
    """
    Sugere refeições inteligentes baseadas em:
    - Metas calóricas do usuário
    - Preferências alimentares
    - Alimentos disponíveis
    - Histórico de refeições
    """
    
    # Base de receitas saudáveis
    RECEITAS = {
        'cafe_da_manha': [
            {
                'nome': 'Omelete de Claras com Aveia',
                'ingredientes': ['3 claras', '2 colheres aveia', '1 fatia queijo minas', 'orégano'],
                'calorias': 280,
                'proteinas': 28, 'carboidratos': 25, 'gorduras': 8,
                'tempo_preparo': 10,
                'dificuldade': 'facil'
            },
            {
                'nome': 'Panqueca de Banana Integral',
                'ingredientes': ['1 banana', '2 ovos', '2 colheres aveia', 'canela'],
                'calorias': 320,
                'proteinas': 18, 'carboidratos': 40, 'gorduras': 10,
                'tempo_preparo': 15,
                'dificuldade': 'facil'
            },
            {
                'nome': 'Iogurte com Granola e Frutas',
                'ingredientes': ['1 pote iogurte natural', '2 colheres granola', '1/2 manga', '1 colher mel'],
                'calorias': 290,
                'proteinas': 12, 'carboidratos': 45, 'gorduras': 8,
                'tempo_preparo': 5,
                'dificuldade': 'facil'
            },
            {
                'nome': 'Vitamina de Frutas com Whey',
                'ingredientes': ['1 scoop whey', '1 banana', '200ml leite desnatado', '1 colher aveia'],
                'calorias': 310,
                'proteinas': 35, 'carboidratos': 35, 'gorduras': 3,
                'tempo_preparo': 5,
                'dificuldade': 'facil'
            }
        ],
        'lanche_manha': [
            {
                'nome': 'Mix de Castanhas e Frutas Secas',
                'ingredientes': ['10 castanhas', '5 damascos', '1 colher uva passa'],
                'calorias': 150,
                'proteinas': 5, 'carboidratos': 18, 'gorduras': 8,
                'tempo_preparo': 2,
                'dificuldade': 'facil'
            },
            {
                'nome': 'Fruta com Pasta de Amendoim',
                'ingredientes': ['1 maçã', '1 colher pasta de amendoim'],
                'calorias': 180,
                'proteinas': 7, 'carboidratos': 22, 'gorduras': 8,
                'tempo_preparo': 3,
                'dificuldade': 'facil'
            }
        ],
        'almoco': [
            {
                'nome': 'Frango Grelhado com Arroz Integral e Brócolis',
                'ingredientes': ['150g frango', '4 colheres arroz integral', '1 xícara brócolis', 'azeite'],
                'calorias': 450,
                'proteinas': 42, 'carboidratos': 45, 'gorduras': 10,
                'tempo_preparo': 25,
                'dificuldade': 'medio'
            },
            {
                'nome': 'Salmão com Quinoa e Legumes',
                'ingredientes': ['150g salmão', '3 colheres quinoa', '1 xícara legumes variados'],
                'calorias': 480,
                'proteinas': 38, 'carboidratos': 35, 'gorduras': 18,
                'tempo_preparo': 30,
                'dificuldade': 'medio'
            },
            {
                'nome': 'Strogonoff de Frango Fit',
                'ingredientes': ['150g frango', '1 colher creme de leite light', '1/2 xícara arroz', 'cogumelos'],
                'calorias': 420,
                'proteinas': 38, 'carboidratos': 40, 'gorduras': 12,
                'tempo_preparo': 20,
                'dificuldade': 'facil'
            },
            {
                'nome': 'Panela de Peixe com Legumes',
                'ingredientes': ['200g merluza', 'batata doce', 'cenoura', 'abobrinha', 'tomate'],
                'calorias': 400,
                'proteinas': 35, 'carboidratos': 38, 'gorduras': 12,
                'tempo_preparo': 35,
                'dificuldade': 'medio'
            }
        ],
        'lanche_tarde': [
            {
                'nome': 'Smoothie Verde',
                'ingredientes': ['1 copo leite coco', '1 folha couve', '1/2 maçã', 'gengibre', '1 scoop whey'],
                'calorias': 200,
                'proteinas': 25, 'carboidratos': 15, 'gorduras': 5,
                'tempo_preparo': 5,
                'dificuldade': 'facil'
            },
            {
                'nome': 'Ovos Mexidos com Pão Integral',
                'ingredientes': ['2 ovos', '1 fatia pão integral', 'tomate cereja', 'cream cheese light'],
                'calorias': 250,
                'proteinas': 18, 'carboidratos': 20, 'gorduras': 12,
                'tempo_preparo': 8,
                'dificuldade': 'facil'
            }
        ],
        'jantar': [
            {
                'nome': 'Sopa Detox de Legumes',
                'ingredientes': ['abóbora', 'cenoura', 'batata doce', 'gengibre', 'salsinha'],
                'calorias': 280,
                'proteinas': 8, 'carboidratos': 45, 'gorduras': 5,
                'tempo_preparo': 25,
                'dificuldade': 'facil'
            },
            {
                'nome': 'Salada Completa com Proteína',
                'ingredientes': ['alface', 'rúcula', 'tomate', '150g frango', 'abacate', 'castanhas'],
                'calorias': 380,
                'proteinas': 35, 'carboidratos': 15, 'gorduras': 20,
                'tempo_preparo': 15,
                'dificuldade': 'facil'
            },
            {
                'nome': 'Wrap Integral de Frango',
                'ingredientes': ['1 wrap integral', '150g frango desfiado', 'alface', 'tomate', 'cream cheese light'],
                'calorias': 350,
                'proteinas': 35, 'carboidratos': 30, 'gorduras': 10,
                'tempo_preparo': 10,
                'dificuldade': 'facil'
            }
        ]
    }

    @classmethod
    def sugerir_refeicoes_do_dia(cls, calorias_meta: float, preferencias: Optional[List[str]] = None) -> Dict:
        """
        Sugere um dia completo de refeições baseado nas calorias meta.
        
        Args:
            calorias_meta: Total de calorias para o dia
            preferencias: Lista de preferências (opcional)
        
        Returns:
            Dict com sugestões para café, almoço, jantar e lanches
        """
        # Distribuição calórica ideal
        distribuicao = {
            'cafe_da_manha': 0.20,  # 20%
            'lanche_manha': 0.10,   # 10%
            'almoco': 0.35,         # 35%
            'lanche_tarde': 0.10,   # 10%
            'jantar': 0.25          # 25%
        }
        
        sugestoes = {}
        calorias_restantes = calorias_meta
        
        for refeicao, percentual in distribuicao.items():
            calorias_ref = round(calorias_meta * percentual, 0)
            opcoes = cls.RECEITAS.get(refeicao, [])
            
            if opcoes:
                # Filtra por preferências se necessário
                opcoes_filtradas = opcoes
                if preferencias:
                    opcoes_filtradas = [
                        r for r in opcoes 
                        if any(p.lower() in ' '.join(r['ingredientes']).lower() for p in preferencias)
                    ]
                    if not opcoes_filtradas:
                        opcoes_filtradas = opcoes
                
                # Escolhe a receita mais próxima das calorias alvo
                melhor_opcao = min(opcoes_filtradas, key=lambda r: abs(r['calorias'] - calorias_ref))
                sugestoes[refeicao] = melhor_opcao
                calorias_restantes -= melhor_opcao['calorias']
        
        return {
            'date': '',
            'total_calorias': calorias_meta,
            'calorias_sugeridas': round(calorias_meta - calorias_restantes, 1),
            'calorias_restantes': round(max(0, calorias_restantes), 1),
            'refeicoes': sugestoes
        }

    @classmethod
    def sugerir_substituicao(cls, receita_original: str) -> Dict:
        """
        Sugere uma versão mais saudável de uma receita.
        
        Args:
            receita_original: Nome da receita que quer substituir
        
        Returns:
            Dict com sugestão de substituição
        """
        substituicoes = {
            'arroz branco': {
                'substituto': 'arroz integral',
                'economia_calorias': 20,
                'beneficio': 'Mais fibras e nutrientes, menor índice glicêmico'
            },
            'refrigerante': {
                'substituto': 'água com gás + limão',
                'economia_calorias': 140,
                'beneficio': 'Zero calorias, hidratação natural'
            },
            'frituras': {
                'substituto': 'alimentos grelhados ou assados',
                'economia_calorias': 150,
                'beneficio': 'Menos gordura saturada e calorias'
            },
            'açúcar refinado': {
                'substituto': 'mel ou adoçante natural',
                'economia_calorias': 30,
                'beneficio': 'Menos processado, nutrientes adicionais'
            },
            'maionese': {
                'substituto': 'cream cheese light ou iogurte natural',
                'economia_calorias': 80,
                'beneficio': 'Menos gordura e calorias'
            },
            'leite integral': {
                'substituto': 'leite desnatado ou vegetal',
                'economia_calorias': 60,
                'beneficio': 'Menos gordura saturada'
            }
        }
        
        substituto = substituicoes.get(receita_original.lower())
        
        if substituto:
            return {
                'success': True,
                'original': receita_original,
                'substituto': substituto['substituto'],
                'economia_calorias': substituto['economia_calorias'],
                'beneficio': substituto['beneficio']
            }
        else:
            return {
                'success': False,
                'message': f'Não encontrei substituição para "{receita_original}"'
            }


# Função de interface para Kotlin
def suggest_meals(calorias_meta: float, preferencias_json: str = '[]') -> str:
    """Função chamada pelo Kotlin para sugerir refeições."""
    preferencias = json.loads(preferencias_json)
    sugestoes = MealSuggester.sugerir_refeicoes_do_dia(calorias_meta, preferencias)
    return json.dumps({'success': True, 'dados': sugestoes})

def suggest_substitution(receita: str) -> str:
    """Função chamada pelo Kotlin para sugerir substituições."""
    resultado = MealSuggester.sugerir_substituicao(receita)
    return json.dumps(resultado)
