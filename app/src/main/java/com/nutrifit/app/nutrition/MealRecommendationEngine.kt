package com.nutrifit.app.nutrition

import android.content.Context
import com.nutrifit.app.data.local.entities.MealScheduleEntity
import com.nutrifit.app.data.repository.NutriFitRepository

/**
 * Sistema de recomendações inteligentes baseado no perfil do usuário,
 * objetivo (emagrecer/manter/ganhar massa) e tipo de refeição.
 */
data class RefeicaoRecomendada(
    val nome: String,
    val descricao: String,
    val explicacao: String, // Por que este alimento ajuda no objetivo
    val calorias: Int,
    val proteinas: Int,
    val carboidratos: Int,
    val gorduras: Int,
    val fibras: Int = 0,
    val tipo: String,
    val sugestoesVariacao: List<String> = emptyList()
)

object MealRecommendationEngine {

    /**
     * Obtém recomendações para uma refeição específica baseada no perfil.
     * @param schedule - agendamento da refeição
     * @param objetivo - emagrecer, manter, ganhar_massa
     * @param caloriasMeta - meta calórica diária calculada pelo Python
     * @param peso - peso atual do usuário
     */
    fun getRecommendation(
        schedule: MealScheduleEntity,
        objetivo: String,
        caloriasMeta: Double,
        peso: Double
    ): RefeicaoRecomendada {
        return when (schedule.tipo) {
            "cafe_da_manha" -> recomendarCafe(objetivo, caloriasMeta)
            "lanche_manha" -> recomendarLancheManha(objetivo, caloriasMeta)
            "almoco" -> recomendarAlmoco(objetivo, caloriasMeta, peso)
            "lanche_tarde" -> recomendarLancheTarde(objetivo, caloriasMeta)
            "jantar" -> recomendarJantar(objetivo, caloriasMeta)
            "ceia" -> recomendarCeia(objetivo)
            else -> recomendarCafe(objetivo, caloriasMeta)
        }
    }

    /**
     * Explicação nutricional detalhada de cada macronutriente.
     */
    fun getExplicacaoNutriente(nutriente: String): String {
        return when (nutriente) {
            "proteinas" -> "🥩 **Proteínas**: Essenciais para construir e reparar músculos. " +
                "Ajudam na saciedade e evitam a perda muscular durante o emagrecimento. " +
                "Fontes: frango, ovos, peixe, leguminosas."

            "carboidratos" -> "🍚 **Carboidratos**: Principal fonte de energia do corpo. " +
                "Os carboidratos complexos (integrais) liberam energia aos poucos, " +
                "mantendo você saciado por mais tempo. Fontes: arroz integral, batata doce, aveia."

            "gorduras" -> "🥑 **Gorduras Boas**: Essenciais para absorção de vitaminas " +
                "e produção hormonal. As gorduras insaturadas (abacate, azeite, castanhas) " +
                "são benéficas para o coração."

            "fibras" -> "🥦 **Fibras**: Regulam o intestino e aumentam a saciedade. " +
                "Ajudam a controlar o açúcar no sangue e reduzem a absorção de gordura. " +
                "Fontes: verduras, frutas com casca, aveia, chia."

            "calorias" -> "🔥 **Calorias**: Unidade de energia dos alimentos. " +
                "Para emagrecer: consuma menos calorias do que gasta. " +
                "Para ganhar massa: consuma mais calorias do que gasta. " +
                "Para manter: equilibre consumo e gasto."

            else -> "Nutriente essencial para o bom funcionamento do organismo."
        }
    }

    // ==================== RECOMENDAÇÕES POR OBJETIVO ====================

    private fun recomendarCafe(objetivo: String, caloriasMeta: Double): RefeicaoRecomendada {
        val base = when (objetivo) {
            "emagrecer" -> RefeicaoRecomendada(
                nome = "🥣 Omelete de Claras com Aveia e Frutas",
                descricao = "2 claras + 1 ovo inteiro, 3 colheres de aveia, 1 banana e café sem açúcar",
                explicacao = "🎯 **Por que isso ajuda a EMAGRECER?**\n\n" +
                    "• As claras fornecem proteína de alto valor biológico com **poucas calorias** (~70kcal)\n" +
                    "• A aveia tem **fibras solúveis** que formam um gel no estômago, aumentando a saciedade\n" +
                    "• A banana fornece **potássio** para evitar cãibras e energia para o dia\n" +
                    "• Café sem açúcar acelera o **metabolismo** em até 5% por 2-3 horas\n\n" +
                    "📊 **Distribuição ideal:** Proteínas (40%) >> Fibras (35%) >> Carboidratos (25%)",
                calorias = 280,
                proteinas = 28,
                carboidratos = 35,
                gorduras = 8,
                fibras = 6,
                tipo = "cafe_da_manha",
                sugestoesVariacao = listOf(
                    "🥛 Iogurte natural com granola sem açúcar",
                    "🥑 Torrada integral com abacate e ovo pochê",
                    "🫐 Smoothie de whey com morango e chia"
                )
            )
            "ganhar_massa" -> RefeicaoRecomendada(
                nome = "🥣 Panqueca Proteica com Banana e Pasta de Amendoim",
                descricao = "3 ovos, 4 colheres de aveia, 1 scoop de whey, 1 banana, 1 colher de pasta de amendoim",
                explicacao = "💪 **Por que isso ajuda a GANHAR MASSA?**\n\n" +
                    "• **45g de proteína** neste café da manhã → estímulo máximo de síntese muscular\n" +
                    "• Pasta de amendoim: **gorduras boas** + calorias densas para superávit calórico\n" +
                    "• Aveia e banana: **carboidratos complexos** para energia no treino matinal\n" +
                    "• Whey protein: **absorção rápida** → aminoácidos disponíveis imediatamente\n\n" +
                    "📊 **Distribuição ideal:** Proteínas (35%) >> Carboidratos (40%) >> Gorduras (25%)",
                calorias = 520,
                proteinas = 45,
                carboidratos = 48,
                gorduras = 18,
                fibras = 4,
                tipo = "cafe_da_manha",
                sugestoesVariacao = listOf(
                    "🥛 Mingau de aveia com whey e pasta de amendoim",
                    "🥚 3 ovos mexidos com queijo cottage e batata doce",
                    "🥞 Crepioca com frango desfiado"
                )
            )
            else -> RefeicaoRecomendada( // manter
                nome = "🥣 Ovos Mexidos com Torrada Integral e Abacate",
                descricao = "2 ovos mexidos, 2 fatias de pão integral, 1/2 abacate, café ou chá",
                explicacao = "⚖️ **Por que isso ajuda a MANTER o peso?**\n\n" +
                    "• **Equilíbrio perfeito** de macronutrientes (~30% cada)\n" +
                    "• Ovos: proteína completa com todos os **aminoácidos essenciais**\n" +
                    "• Abacate: **gordura monoinsaturada** → saciedade prolongada\n" +
                    "• Pão integral: **carboidrato de baixo índice glicêmico** → energia estável\n\n" +
                    "📊 **Distribuição ideal:** Equilíbrio 33/33/33 entre os 3 macronutrientes",
                calorias = 380,
                proteinas = 24,
                carboidratos = 30,
                gorduras = 18,
                fibras = 5,
                tipo = "cafe_da_manha",
                sugestoesVariacao = listOf(
                    "🥣 Granola caseira com iogurte grego e mel",
                    "🥚 Wrap integral com ovos e vegetais",
                    "🫐 Vitamina de banana com aveia e leite"
                )
            )
        }
        return base
    }

    private fun recomendarLancheManha(objetivo: String, caloriasMeta: Double): RefeicaoRecomendada {
        return when (objetivo) {
            "emagrecer" -> RefeicaoRecomendada(
                nome = "🍎 Maçã com Pasta de Amendoim e Canela",
                descricao = "1 maçã grande + 1 colher de pasta de amendoim pura + canela",
                explicacao = "🎯 **Por que este lanche ajuda a EMAGRECER?**\n\n" +
                    "• **~120kcal** → lanche leve que não estoura a meta calórica\n" +
                    "• Maçã: rica em **pectina** (fibra solúvel) que reduz absorção de gordura\n" +
                    "• Canela: ajuda a **estabilizar o açúcar no sangue** → menos fome mais tarde\n" +
                    "• Pasta de amendoim: **proteína + gordura boa** → segura a fome até o almoço\n\n" +
                    "💡 **Dica:** Beba 1 copo de água junto para aumentar a saciedade!",
                calorias = 150,
                proteinas = 5,
                carboidratos = 22,
                gorduras = 6,
                fibras = 4,
                tipo = "lanche_manha",
                sugestoesVariacao = listOf(
                    "🥤 Iogurte natural desnatado com chia",
                    "🥜 Mix de castanhas (10 unidades)",
                    "🍵 Chá verde com 3 bolachas de arroz"
                )
            )
            "ganhar_massa" -> RefeicaoRecomendada(
                nome = "🥤 Shake Hipercalórico de Banana e Whey",
                descricao = "1 scoop de whey, 1 banana, 1 colher de pasta de amendoim, 200ml leite, 1 colher de aveia",
                explicacao = "💪 **Por que isso ajuda a GANHAR MASSA?**\n\n" +
                    "• **350kcal em um shake** → fácil de consumir mesmo sem fome\n" +
                    "• 30g de proteína → **janela anabólica** aberta após o treino\n" +
                    "• Pasta de amendoim: alto teor calórico para o **superávit necessário**\n" +
                    "• Banana: **potássio** + carboidrato rápido para reposição de glicogênio\n\n" +
                    "📊 **30g proteína | 40g carboidratos | 12g gorduras**",
                calorias = 350,
                proteinas = 30,
                carboidratos = 40,
                gorduras = 12,
                fibras = 2,
                tipo = "lanche_manha",
                sugestoesVariacao = listOf(
                    "🥜 Pão integral com pasta de amendoim e banana",
                    "🥛 Leite com achocolatado + 1 scoop de whey",
                    "🫐 Vitamina de frutas com aveia"
                )
            )
            else -> RefeicaoRecomendada(
                nome = "🍌 Iogurte grego com Banana e Granola",
                descricao = "1 pote de iogurte grego, 1 banana, 2 colheres de granola sem açúcar",
                explicacao = "⚖️ **Lanche equilibrado para MANUTENÇÃO:**\n\n" +
                    "• **Proteína do iogurte** → mantém sua massa muscular\n" +
                    "• **Carboidrato da banana** → energia para o resto da manhã\n" +
                    "• **Granola com fibras** → digestão lenta, saciedade prolongada\n" +
                    "• Probióticos do iogurte → **saúde intestinal** fortalecida\n\n" +
                    "💡 **Versão low carb:** Troque banana por morangos e granola por castanhas",
                calorias = 280,
                proteinas = 15,
                carboidratos = 32,
                gorduras = 10,
                fibras = 3,
                tipo = "lanche_manha",
                sugestoesVariacao = listOf(
                    "🥪 Pão integral com queijo minas e orégano",
                    "🥤 Suco natural com chia",
                    "🥜 15g de castanhas + 1 fruta"
                )
            )
        }
    }

    private fun recomendarAlmoco(objetivo: String, caloriasMeta: Double, peso: Double): RefeicaoRecomendada {
        val protBase = if (objetivo == "ganhar_massa") (peso * 0.4).toInt() else (peso * 0.3).toInt()
        return when (objetivo) {
            "emagrecer" -> RefeicaoRecomendada(
                nome = "🥗 Frango Grelhado com Quinoa e Brócolis",
                descricao = "200g filé de frango + 1/2 xícara quinoa + brócolis refogado + cenoura",
                explicacao = "🎯 **Por que este almoço EMAGRECE?**\n\n" +
                    "• **Alto teor proteico (${protBase}g)** → maior termogênese (gasta calorias para digerir)\n" +
                    "• Quinoa: **proteína completa** + carboidrato de baixo índice glicêmico\n" +
                    "• Brócolis: **rico em fibras e cálcio** → ajuda na quebra de gordura\n" +
                    "• Sem frituras! Apenas **vegetais crus/grelhados** → baixa densidade calórica\n\n" +
                    "🔥 **Efeito térmico:** Você gasta ~25% das calorias da proteína só para digeri-la!\n" +
                    "📊 **~400kcal | ${protBase}g proteína | 8g fibras**",
                calorias = 400,
                proteinas = protBase,
                carboidratos = 28,
                gorduras = 10,
                fibras = 8,
                tipo = "almoco",
                sugestoesVariacao = listOf(
                    "🐟 Tilápia grelhada com batata doce e couve",
                    "🥩 Carne magra com arroz integral e salada verde",
                    "🥦 Strogonoff de frango fit com cogumelos"
                )
            )
            "ganhar_massa" -> RefeicaoRecomendada(
                nome = "🐟 Salmão com Batata Doce e Abacate",
                descricao = "200g filé de salmão + 1 batata doce grande + 1/2 abacate + salada",
                explicacao = "💪 **Por que isso GANHA MASSA?**\n\n" +
                    "• **Salmão: proteína + ômega-3** → reduz inflamação pós-treino e ajuda recuperação\n" +
                    "• Batata doce: **carboidrato de alto índice glicêmico** → repõe glicogênio rápido\n" +
                    "• Abacate: **400kcal de gordura boa** → superávit calórico de qualidade\n" +
                    "• **~${(peso*0.5).toInt()}g de proteína** → estímulo máximo de hipertrofia\n\n" +
                    "🔥 **Calorias totais:** ~650kcal (perfeito para quem precisa de superávit)\n" +
                    "📊 **Distribuição:** Proteínas 30% | Carboidratos 35% | Gorduras 35%",
                calorias = 650,
                proteinas = (peso * 0.5).toInt(),
                carboidratos = 55,
                gorduras = 28,
                fibras = 6,
                tipo = "almoco",
                sugestoesVariacao = listOf(
                    "🍝 Macarrão integral com almôndegas de carne",
                    "🥩 Bife de alcatra com arroz e feijão",
                    "🍗 Frango com batata salsa e legumes"
                )
            )
            else -> RefeicaoRecomendada(
                nome = "🥩 Bife Grelhado com Arroz Integral e Legumes",
                descricao = "150g carne magra + 4 colheres arroz integral + legumes salteados + salada",
                explicacao = "⚖️ **Almoço equilibrado para MANTER:**\n\n" +
                    "• **Proteína magra** → manutenção muscular sem excesso calórico\n" +
                    "• Arroz integral: **carboidrato complexo** → energia de liberação lenta\n" +
                    "• Legumes variados: **vitaminas, minerais e fibras** essenciais\n" +
                    "• **~500kcal** → ponto ideal para uma refeição principal de manutenção\n\n" +
                    "🍽️ **Dica:** Tempere com ervas frescas (alecrim, salsinha) ao invés de molhos prontos\n" +
                    "📊 **Macros balanceados:** 33g prot | 42g carb | 15g gord",
                calorias = 500,
                proteinas = 33,
                carboidratos = 42,
                gorduras = 15,
                fibras = 7,
                tipo = "almoco",
                sugestoesVariacao = listOf(
                    "🥗 Salada completa com frango grelhado e ovo",
                    "🍛 Frango ao curry com arroz basmati",
                    "🥘 Legumes assados com tofu e quinoa"
                )
            )
        }
    }

    private fun recomendarLancheTarde(objetivo: String, caloriasMeta: Double): RefeicaoRecomendada {
        return when (objetivo) {
            "emagrecer" -> RefeicaoRecomendada(
                nome = "🥤 Iogurte Natural com Chia e Morangos",
                descricao = "1 pote de iogurte natural desnatado + 1 colher de chia + 5 morangos",
                explicacao = "🎯 **Por que este lanche EMAGRECE?**\n\n" +
                    "• **Chia:** absorve até 10x seu peso em água → forma um gel que **expande no estômago**\n" +
                    "• Iogurte natural: **probióticos** melhoram a flora intestinal (ligada ao metabolismo)\n" +
                    "• Morangos: baixas calorias + **vitamina C** → ajuda na queima de gordura\n" +
                    "• **Apenas 120kcal** → não compromete a contagem do dia\n\n" +
                    "💡 **Dica para fome intensa:** Adicione 1 colher de whey para mais saciedade!",
                calorias = 120,
                proteinas = 10,
                carboidratos = 14,
                gorduras = 4,
                fibras = 5,
                tipo = "lanche_tarde",
                sugestoesVariacao = listOf(
                    "🥒 Palitos de cenoura e pepino com homus",
                    "🫐 1 fruta + 5 castanhas",
                    "🥚 1 ovo cozido com 1 torrada integral"
                )
            )
            "ganhar_massa" -> RefeicaoRecomendada(
                nome = "🥪 Sanduíche de Frango com Pasta de Amendoim",
                descricao = "2 fatias pão integral + 100g frango desfiado + 1 colher pasta de amendoim + alface",
                explicacao = "💪 **Por que isso GANHA MASSA?**\n\n" +
                    "• **Duas fontes de proteína** (frango + pasta de amendoim) → síntese prolongada\n" +
                    "• Pasta de amendoim: **alta densidade calórica** para ajudar no superávit\n" +
                    "• Pão integral: carboidrato para **energia no treino da tarde**\n" +
                    "• **~380kcal perfeitos** para um lanche hipercalórico\n\n" +
                    "🔥 **Ideal para consumir 1-2h antes do treino!**\n" +
                    "📊 **32g proteína | 28g carboidratos | 14g gorduras**",
                calorias = 380,
                proteinas = 32,
                carboidratos = 28,
                gorduras = 14,
                fibras = 3,
                tipo = "lanche_tarde",
                sugestoesVariacao = listOf(
                    "🥤 Shake de whey com pasta de amendoim e banana",
                    "🥚 3 ovos mexidos com queijo e pão",
                    "🥜 Wrap de frango com cream cheese"
                )
            )
            else -> RefeicaoRecomendada(
                nome = "🥑 Torrada Integral com Abacate e Ovo",
                descricao = "2 fatias pão integral + 1/2 abacate amassado + 1 ovo pochê + limão",
                explicacao = "⚖️ **Lanche equilibrado para MANTER:**\n\n" +
                    "• **Gordura monoinsaturada do abacate** → benéfica para o coração\n" +
                    "• Ovo: **proteína completa** + colina (importante para o cérebro)\n" +
                    "• Pão integral: **fibras** para manter o intestino regulado\n" +
                    "• Limão: **vitamina C** + ajuda na absorção de nutrientes\n\n" +
                    "🍽️ **~250kcal** → tamanho ideal de lanche para manutenção\n" +
                    "📊 **12g proteína | 22g carboidratos | 14g gorduras**",
                calorias = 250,
                proteinas = 12,
                carboidratos = 22,
                gorduras = 14,
                fibras = 4,
                tipo = "lanche_tarde",
                sugestoesVariacao = listOf(
                    "🥛 Smoothie de frutas com iogurte",
                    "🥜 Castanhas com fruta seca e queijo",
                    "🥟 Pastel integral assado de frango"
                )
            )
        }
    }

    private fun recomendarJantar(objetivo: String, caloriasMeta: Double): RefeicaoRecomendada {
        return when (objetivo) {
            "emagrecer" -> RefeicaoRecomendada(
                nome = "🥗 Salada Completa com Atum e Ovo",
                descricao = "Mix de folhas + 1 lata de atum + 1 ovo cozido + tomate + cenoura + azeite",
                explicacao = "🎯 **Por que este jantar EMAGRECE?**\n\n" +
                    "• **Refeição leve e de alta saciedade** → menos calorias antes de dormir\n" +
                    "• Atum: proteína magra com **ômega-3** → anti-inflamatório natural\n" +
                    "• Ovo + folhas verdes: baixa caloria mas **alto volume** no estômago\n" +
                    "• Dormir de estômago leve → **melhor qualidade do sono** → regula hormônios da fome\n\n" +
                    "🌙 **Regra de ouro:** Jantar até 3h antes de dormir para melhor digestão!\n" +
                    "🔥 **Apenas 300kcal** → perfeito para manter o déficit calórico",
                calorias = 300,
                proteinas = 28,
                carboidratos = 10,
                gorduras = 14,
                fibras = 6,
                tipo = "jantar",
                sugestoesVariacao = listOf(
                    "🍤 Camarão grelhado com legumes no vapor",
                    "🥦 Sopa de legumes com frango desfiado",
                    "🐟 Tilápia com purê de couve-flor"
                )
            )
            "ganhar_massa" -> RefeicaoRecomendada(
                nome = "🍝 Macarrão Integral com Carne Moída e Molho de Tomate",
                descricao = "200g macarrão integral + 150g carne moída magra + molho de tomate caseiro",
                explicacao = "💪 **Por que isso GANHA MASSA?**\n\n" +
                    "• **Carboidrato em abundância** → repõe glicogênio muscular para o dia seguinte\n" +
                    "• Carne moída: **ferro + zinco** → essenciais para produção hormonal (testosterona)\n" +
                    "• Molho de tomate: **licopeno** (antioxidante) + baixas calorias para dar sabor\n" +
                    "• **~600kcal** → jantar calórico ideal para superávit\n\n" +
                    "💪 **Dica anabólica:** Adicione 1 ovo por cima para proteína extra!\n" +
                    "📊 **38g proteína | 65g carboidratos | 16g gorduras**",
                calorias = 600,
                proteinas = 38,
                carboidratos = 65,
                gorduras = 16,
                fibras = 5,
                tipo = "jantar",
                sugestoesVariacao = listOf(
                    "🍗 Frango assado com batatas e legumes",
                    "🥩 Carne de panela com mandioca e salada",
                    "🍚 Arroz de forno com frango e queijo"
                )
            )
            else -> RefeicaoRecomendada(
                nome = "🍲 Sopa de Legumes com Frango",
                descricao = "Sopa de abóbora, cenoura, batata doce + 150g frango desfiado + gengibre",
                explicacao = "⚖️ **Jantar equilibrado para MANTER:**\n\n" +
                    "• **Sopa:** alta hidratação + baixa densidade calórica + aquecimento do corpo\n" +
                    "• Frango desfiado: **proteína magra** sem excesso de gordura\n" +
                    "• Gengibre: **termogênico natural** → acelera levemente o metabolismo\n" +
                    "• Abóbora: rica em **vitamina A** e antioxidantes\n\n" +
                    "🌙 **Benefício:** Sopa quente à noite ajuda a relaxar e melhora o sono!\n" +
                    "📊 **~380kcal | 32g proteína | 12g gorduras**",
                calorias = 380,
                proteinas = 32,
                carboidratos = 30,
                gorduras = 12,
                fibras = 7,
                tipo = "jantar",
                sugestoesVariacao = listOf(
                    "🥗 Salada de grão-de-bico com atum",
                    "🍳 Omelete de claras com espinafre",
                    "🥦 Legumes assados com tofu"
                )
            )
        }
    }

    private fun recomendarCeia(objetivo: String): RefeicaoRecomendada {
        return when (objetivo) {
            "emagrecer" -> RefeicaoRecomendada(
                nome = "🫖 Chá de Camomila com Canela",
                descricao = "1 xícara de chá de camomila + 1 pau de canela (ou canela em pó)",
                explicacao = "🎯 **Por que esta ceia EMAGRECE?**\n\n" +
                    "• **~5kcal** → praticamente zero calorias, não atrapalha o déficit\n" +
                    "• Camomila: efeito **calmante e relaxante** → melhora qualidade do sono\n" +
                    "• Canela: **regula o açúcar no sangue** → evita picos de insulina durante a noite\n" +
                    "• Sono de qualidade → **GH (hormônio do crescimento)** mais ativo → queima de gordura\n\n" +
                    "🌙 **Regra de ouro:** Não coma carboidratos 2h antes de dormir para emagrecer!",
                calorias = 5,
                proteinas = 0,
                carboidratos = 1,
                gorduras = 0,
                fibras = 0,
                tipo = "ceia",
                sugestoesVariacao = listOf(
                    "🫖 Chá de hortelã (calmante e digestivo)",
                    "💧 1 copo de água morna com limão",
                    "🍵 Chá de gengibre (termogênico)"
                )
            )
            "ganhar_massa" -> RefeicaoRecomendada(
                nome = "🥛 Caseína com Leite e Canela",
                descricao = "1 scoop de caseína + 200ml leite + canela (ou 1 pote de iogurte grego com mel)",
                explicacao = "💪 **Por que isso GANHA MASSA?**\n\n" +
                    "• **Caseína:** proteína de absorção lenta (6-8h) → **alimenta os músculos a noite toda**\n" +
                    "• Enquanto você dorme, seu corpo **repara as fibras musculares** danificadas no treino\n" +
                    "• Leite: **cálcio** + proteína extra + triptofano (ajuda a dormir melhor)\n" +
                    "• **~200kcal** → nutrição contínua sem atrapalhar a digestão\n\n" +
                    "🌙 **Dica:** Tome 30 min antes de dormir para melhor absorção!",
                calorias = 200,
                proteinas = 28,
                carboidratos = 12,
                gorduras = 5,
                fibras = 0,
                tipo = "ceia",
                sugestoesVariacao = listOf(
                    "🥛 Iogurte grego com mel e granola",
                    "🥜 Pasta de amendoim com 1 banana",
                    "🫖 Leite morno com mel e canela"
                )
            )
            else -> RefeicaoRecomendada(
                nome = "🫖 Chá de Ervas com 2 Bolachas de Arroz",
                descricao = "1 xícara de chá de ervas (camomila, hortelã ou frutas vermelhas) + 2 bolachas de arroz",
                explicacao = "⚖️ **Ceia leve para MANTER:**\n\n" +
                    "• **Apenas 60kcal** → não vai atrapalhar sua manutenção calórica\n" +
                    "• Chá de ervas: **relaxante natural** → prepara o corpo para o sono\n" +
                    "• Bolacha de arroz: **carboidrato simples** que não sobrecarrega a digestão\n\n" +
                    "🌙 **Dica:** Evite telas (celular/TV) 30 min antes de dormir para um sono reparador!",
                calorias = 60,
                proteinas = 1,
                carboidratos = 12,
                gorduras = 0,
                fibras = 0,
                tipo = "ceia",
                sugestoesVariacao = listOf(
                    "🫖 1 xícara de leite morno",
                    "💧 Água aromatizada com hortelã",
                    "🍵 Chá de frutas vermelhas"
                )
            )
        }
    }
}