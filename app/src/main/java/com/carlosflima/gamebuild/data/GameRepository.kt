package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

interface GameRepository {
    fun getCharacters(game: Game): List<GameCharacter>
    fun getBuilds(characterId: String): List<CharacterBuild>
}

class FakeGameRepository : GameRepository {
    override fun getCharacters(game: Game): List<GameCharacter> = when (game) {
        Game.NTE -> listOf(
            GameCharacter("nte-demo-1", "Personagem NTE 01", "DPS", Game.NTE),
            GameCharacter("nte-demo-2", "Personagem NTE 02", "Suporte", Game.NTE)
        )
        Game.WARFRAME -> listOf(
            GameCharacter("wf-demo-1", "Warframe Demo 01", "DPS", Game.WARFRAME),
            GameCharacter("wf-demo-2", "Warframe Demo 02", "Suporte", Game.WARFRAME)
        )
        Game.ENDFIELD -> listOf(
            GameCharacter("ef-demo-1", "Operador Demo 01", "DPS", Game.ENDFIELD),
            GameCharacter("ef-demo-2", "Operador Demo 02", "Suporte", Game.ENDFIELD)
        )
    }

    override fun getBuilds(characterId: String): List<CharacterBuild> = when (characterId) {
        "nte-demo-1" -> listOf(
            CharacterBuild(
                id = "nte-demo-1-meta",
                characterId = characterId,
                title = "DPS principal",
                type = BuildType.META,
                version = "V0.2 demo",
                weapon = "Arma recomendada (placeholder)",
                equipment = listOf("Conjunto ofensivo", "Peça de crítico", "Peça de dano"),
                statPriority = listOf("Taxa crítica", "Dano crítico", "ATK"),
                team = listOf("Personagem NTE 01", "Suporte NTE", "Flex"),
                notes = "Estrutura preparada para receber dados reais e atualização por versão.",
                sources = listOf(BuildSource("Fonte oficial / guia — pendente"))
            ),
            CharacterBuild(
                id = "nte-demo-1-f2p",
                characterId = characterId,
                title = "Alternativa acessível",
                type = BuildType.F2P,
                version = "V0.2 demo",
                weapon = "Arma F2P (placeholder)",
                equipment = listOf("Conjunto acessível", "Peça de ATK"),
                statPriority = listOf("ATK", "Taxa crítica", "Dano crítico"),
                team = listOf("Personagem NTE 01", "Suporte acessível", "Flex"),
                notes = "Opção demonstrativa para validar comparação entre Meta e F2P.",
                sources = listOf(BuildSource("Fonte comunitária — pendente"))
            )
        )
        "nte-demo-2" -> listOf(
            CharacterBuild(
                id = "nte-demo-2-support",
                characterId = characterId,
                title = "Suporte geral",
                type = BuildType.F2P,
                version = "V0.2 demo",
                weapon = "Arma de suporte (placeholder)",
                equipment = listOf("Conjunto de suporte", "Recarga/Utilidade"),
                statPriority = listOf("Recarga", "Utilidade", "ATK"),
                team = listOf("DPS", "Personagem NTE 02", "Flex"),
                notes = "Build demonstrativa de suporte.",
                sources = listOf(BuildSource("Fonte — pendente"))
            )
        )
        else -> emptyList()
    }
}
