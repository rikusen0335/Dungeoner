package net.rikusen.dungeoner.dungeon_generator.graph_based_generator.common.chain_decomposition

// Reference: https://github.com/OndrejNepozitek/Edgar-DotNet/blob/dev/src/Edgar/GraphBasedGenerator/Common/ChainDecomposition/Chain.cs
data class Chain<TNode>(val nodes: List<TNode>, val number: Int) {
    var isFromFace: Boolean = false

    constructor(nodes: List<TNode>, number: Int, isFromFace: Boolean) : this(nodes, number) {
        this.isFromFace = isFromFace
    }
}