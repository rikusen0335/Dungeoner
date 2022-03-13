package net.rikusen.dungeoner.dungeon_generator.legacy.core.layout_generators

import net.rikusen.dungeoner.dungeon_generator.graph_based_generator.common.chain_decomposition.Chain
import net.rikusen.dungeoner.dungeon_generator.legacy.core.generator_planners.intefaces.IGeneratorPlanner
import net.rikusen.dungeoner.dungeon_generator.legacy.core.layout_converters.interfaces.ILayoutConverters
import net.rikusen.dungeoner.dungeon_generator.legacy.core.layout_evolvers.interfaces.ILayoutEvolver

data class ChainBasedGenerator<TLayout, TOutputLayout, TNode>(
    private val initialLayout: TLayout,
    private val generatorPlanner: IGeneratorPlanner<TLayout, TNode>,
    private val chains: List<Chain<TNode>>,
    private val layoutEvolver: ILayoutEvolver<TLayout, TNode>,
    private val layoutConverter: ILayoutConverters<TLayout, TOutputLayout>
) {
    var iterationsCount: Int = 0
        private set

    private fun iterationsCounterHandler(sender: Any, layout: TLayout) {
        iterationsCount++
    }

    fun generateLayout(): TOutputLayout {
        iterationsCount = 0
        layoutEvolver.onPerturbed += ::iterationsCounterHandler
    }
    public TOutputLayout GenerateLayout()
    {
        IterationsCount = 0;
        layoutEvolver.OnPerturbed += IterationsCounterHandler;

        OnRandomInjected?.Invoke(Random);

        if (CancellationToken.HasValue)
        {
            OnCancellationTokenInjected?.Invoke(CancellationToken.Value);
        }

        var stopwatch = new Stopwatch();
        stopwatch.Start();

        var layout = generatorPlanner.Generate(initialLayout, chains, layoutEvolver);

        stopwatch.Stop();

        layoutEvolver.OnPerturbed -= IterationsCounterHandler;
        LayoutsCount = 1;
        TimeTotal = stopwatch.ElapsedMilliseconds;

        // Reset cancellation token if it was already used
        if (CancellationToken.HasValue && CancellationToken.Value.IsCancellationRequested)
        {
            CancellationToken = null;
        }

        var convertedLayout = layout != null ? layoutConverter.Convert(layout, true) : default(TOutputLayout);

        return convertedLayout;
    }
}