package net.rikusen.dungeoner.dungeon_generator.legacy.core.layout_evolvers.interfaces

import net.rikusen.dungeoner.dungeon_generator.EventHandler
import net.rikusen.dungeoner.dungeon_generator.graph_based_generator.common.chain_decomposition.Chain

// Reference: https://github.com/OndrejNepozitek/Edgar-DotNet/blob/dev/src/Edgar/Legacy/Core/LayoutEvolvers/Interfaces/ILayoutEvolver.cs
interface ILayoutEvolver<TLayout, TNode> {
    val onPerturbed: EventHandler<TLayout>;

    val onValid: EventHandler<TLayout>;

    fun evolve(initialLayout: TLayout, chain: Chain<TNode>, count: Int): Iterator<TLayout>;
}