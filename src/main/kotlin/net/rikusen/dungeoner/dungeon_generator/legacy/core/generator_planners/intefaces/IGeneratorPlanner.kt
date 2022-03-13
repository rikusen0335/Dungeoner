package net.rikusen.dungeoner.dungeon_generator.legacy.core.generator_planners.intefaces

import net.rikusen.dungeoner.dungeon_generator.graph_based_generator.common.chain_decomposition.Chain
import net.rikusen.dungeoner.dungeon_generator.legacy.core.layout_evolvers.interfaces.ILayoutEvolver

interface IGeneratorPlanner<TLayout, TNode> {
    fun generate(
        initialLayout: TLayout,
        chains: List<Chain<TNode>>,
        layoutEvolver: ILayoutEvolver<TLayout, TNode>
    ): TLayout
}