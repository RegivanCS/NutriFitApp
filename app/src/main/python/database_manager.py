"""
NutriFit - Gerenciador de Banco de Dados Python
Operações auxiliares no banco SQLite via Python
"""

import json
import sqlite3
import os
from typing import Dict, List, Optional, Any


class DatabaseManager:
    """
    Gerencia operações no banco SQLite que exigem lógica Python.
    Usado para consultas complexas e análises.
    """
    
    def __init__(self, db_path: str):
        """
        Inicializa o gerenciador.
        
        Args:
            db_path: Caminho completo para o arquivo do banco de dados
        """
        self.db_path = db_path
    
    def _get_connection(self) -> sqlite3.Connection:
        """Cria e retorna uma conexão com o banco."""
        conn = sqlite3.connect(self.db_path)
        conn.row_factory = sqlite3.Row
        return conn
    
    def get_semanal_report(self, user_id: int, data_inicio: str, data_fim: str) -> str:
        """
        Gera relatório semanal completo do usuário.
        
        Args:
            user_id: ID do usuário
            data_inicio: Data início (YYYY-MM-DD)
            data_fim: Data fim (YYYY-MM-DD)
        
        Returns:
            JSON com relatório
        """
        conn = self._get_connection()
        cursor = conn.cursor()
        
        try:
            # Média de calorias consumidas no período
            cursor.execute("""
                SELECT AVG(total_calorias) as media_calorias,
                       AVG(total_proteinas) as media_proteinas,
                       AVG(total_carboidratos) as media_carboidratos,
                       AVG(total_gorduras) as media_gorduras,
                       AVG(total_agua) as media_agua
                FROM diario_refeicoes
                WHERE user_id = ? AND data BETWEEN ? AND ?
            """, (user_id, data_inicio, data_fim))
            
            medias = dict(cursor.fetchone())
            
            # Dias mais consistentes
            cursor.execute("""
                SELECT data, total_calorias,
                       abs(total_calorias - (
                           SELECT AVG(total_calorias) 
                           FROM diario_refeicoes 
                           WHERE user_id = ? AND data BETWEEN ? AND ?
                       )) as desvio
                FROM diario_refeicoes
                WHERE user_id = ? AND data BETWEEN ? AND ?
                ORDER BY desvio ASC
                LIMIT 3
            """, (user_id, data_inicio, data_fim, user_id, data_inicio, data_fim))
            
            dias_consistentes = [dict(row) for row in cursor.fetchall()]
            
            # Total de refeições registradas
            cursor.execute("""
                SELECT COUNT(*) as total_refeicoes,
                       COUNT(DISTINCT data) as dias_com_registro
                FROM diario_refeicoes
                WHERE user_id = ? AND data BETWEEN ? AND ?
            """, (user_id, data_inicio, data_fim))
            
            registros = dict(cursor.fetchone())
            
            # Calcular streak (dias consecutivos)
            cursor.execute("""
                SELECT DISTINCT data 
                FROM diario_refeicoes 
                WHERE user_id = ? 
                ORDER BY data DESC
            """, (user_id,))
            
            datas = [row['data'] for row in cursor.fetchall()]
            streak = self._calcular_streak(datas)
            
            return json.dumps({
                'success': True,
                'relatorio': {
                    'periodo': {'inicio': data_inicio, 'fim': data_fim},
                    'medias': {k: round(v, 1) if v else 0 for k, v in medias.items()},
                    'dias_consistentes': dias_consistentes,
                    'registros': registros,
                    'streak_atual': streak,
                    'total_dias': len(set(datas))
                }
            })
            
        except Exception as e:
            return json.dumps({'success': False, 'error': str(e)})
        finally:
            conn.close()
    
    def _calcular_streak(self, datas: List[str]) -> int:
        """Calcula quantos dias consecutivos o usuário registrou refeições."""
        if not datas:
            return 0
        
        from datetime import datetime, timedelta
        
        datas_dt = [datetime.strptime(d, '%Y-%m-%d') for d in datas]
        datas_dt.sort(reverse=True)
        
        streak = 1
        for i in range(len(datas_dt) - 1):
            diff = (datas_dt[i] - datas_dt[i + 1]).days
            if diff == 1:
                streak += 1
            else:
                break
        
        return streak
    
    def get_alimentos_mais_consumidos(self, user_id: int, limite: int = 10) -> str:
        """
        Retorna os alimentos mais consumidos pelo usuário.
        
        Args:
            user_id: ID do usuário
            limite: Número máximo de alimentos
        
        Returns:
            JSON com lista de alimentos
        """
        conn = self._get_connection()
        cursor = conn.cursor()
        
        try:
            cursor.execute("""
                SELECT a.nome, 
                       COUNT(*) as frequencia,
                       AVG(da.quantidade) as quantidade_media,
                       SUM(da.calorias) as calorias_total
                FROM diario_alimentos da
                JOIN alimentos a ON a.id = da.alimento_id
                JOIN diario_refeicoes dr ON dr.id = da.refeicao_id
                WHERE dr.user_id = ?
                GROUP BY a.nome
                ORDER BY frequencia DESC
                LIMIT ?
            """, (user_id, limite))
            
            alimentos = [dict(row) for row in cursor.fetchall()]
            
            return json.dumps({
                'success': True,
                'alimentos': alimentos
            })
            
        except Exception as e:
            return json.dumps({'success': False, 'error': str(e)})
        finally:
            conn.close()
    
    def analyze_eating_patterns(self, user_id: int) -> str:
        """
        Analisa padrões alimentares do usuário.
        
        Args:
            user_id: ID do usuário
        
        Returns:
            JSON com análise de padrões
        """
        conn = self._get_connection()
        cursor = conn.cursor()
        
        try:
            # Horário médio das refeições
            cursor.execute("""
                SELECT 
                    CASE 
                        WHEN strftime('%H', horario) < '10' THEN 'Café da Manhã'
                        WHEN strftime('%H', horario) BETWEEN '10' AND '13' THEN 'Lanche Manhã'
                        WHEN strftime('%H', horario) BETWEEN '12' AND '15' THEN 'Almoço'
                        WHEN strftime('%H', horario) BETWEEN '15' AND '18' THEN 'Lanche Tarde'
                        ELSE 'Jantar'
                    END as tipo_refeicao,
                    COUNT(*) as quantidade,
                    AVG(total_calorias) as calorias_media
                FROM diario_refeicoes
                WHERE user_id = ?
                GROUP BY tipo_refeicao
                ORDER BY 
                    CASE tipo_refeicao
                        WHEN 'Café da Manhã' THEN 1
                        WHEN 'Lanche Manhã' THEN 2
                        WHEN 'Almoço' THEN 3
                        WHEN 'Lanche Tarde' THEN 4
                        WHEN 'Jantar' THEN 5
                    END
            """, (user_id,))
            
            padroes = [dict(row) for row in cursor.fetchall()]
            
            return json.dumps({
                'success': True,
                'padroes': padroes
            })
            
        except Exception as e:
            return json.dumps({'success': False, 'error': str(e)})
        finally:
            conn.close()
    
    def get_progresso_completo(self, user_id: int) -> str:
        """
        Retorna dados completos de progresso para gráficos.
        
        Args:
            user_id: ID do usuário
        
        Returns:
            JSON com dados de progresso
        """
        conn = self._get_connection()
        cursor = conn.cursor()
        
        try:
            # Histórico de pesagens
            cursor.execute("""
                SELECT data, peso, created_at
                FROM progresso_peso
                WHERE user_id = ?
                ORDER BY data ASC
            """, (user_id,))
            
            historico_peso = [dict(row) for row in cursor.fetchall()]
            
            # Médias semanais de calorias
            cursor.execute("""
                SELECT 
                    strftime('%Y-%W', data) as semana,
                    ROUND(AVG(total_calorias), 1) as media_calorias
                FROM diario_refeicoes
                WHERE user_id = ?
                GROUP BY semana
                ORDER BY semana ASC
            """, (user_id,))
            
            calorias_semanais = [dict(row) for row in cursor.fetchall()]
            
            return json.dumps({
                'success': True,
                'dados': {
                    'historico_peso': historico_peso,
                    'calorias_semanais': calorias_semanais
                }
            })
            
        except Exception as e:
            return json.dumps({'success': False, 'error': str(e)})
        finally:
            conn.close()


# Funções de interface para Kotlin

def generate_report(user_id: int, inicio: str, fim: str, db_path: str) -> str:
    """Gera relatório semanal. Chamada pelo Kotlin."""
    mgr = DatabaseManager(db_path)
    return mgr.get_semanal_report(user_id, inicio, fim)

def analyze_patterns(user_id: int, db_path: str) -> str:
    """Analisa padrões alimentares. Chamada pelo Kotlin."""
    mgr = DatabaseManager(db_path)
    return mgr.analyze_eating_patterns(user_id)

def get_progress(user_id: int, db_path: str) -> str:
    """Retorna dados de progresso. Chamada pelo Kotlin."""
    mgr = DatabaseManager(db_path)
    return mgr.get_progresso_completo(user_id)
