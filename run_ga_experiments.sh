#!/bin/sh

CMD="java -cp target/classes/ com.johnnyleitrim.cpmp.ga.Main -seed 183758 -lowerBound BF -selection Tournament -runs 5 -execute"

MUTATIONS="ClearAndFillStack InvertMoves"
CROSSOVERS="KPoint SameHeightAnyStack"
MUTATION_DELTAS="0.0 0.01"
LOCAL_SEARCH="true false"
BF_CATEGORIES="16 32"

NUM_CONCURRENT_EXPERIMENTS=6

OUTPUT_DIR="output/ga"

mkdir -p "$OUTPUT_DIR"

procs=()

for mutation in $MUTATIONS; do
	for crossover in $CROSSOVERS; do
		for mutation_delta in $MUTATION_DELTAS; do
			for bf in $BF_CATEGORIES; do
				for perform_local_search in $LOCAL_SEARCH; do
					PARAMS="-mutation $mutation -mutationDelta $mutation_delta -crossover $crossover -bf $bf -performLocalSearch $perform_local_search"
					$CMD $PARAMS 2>&1 | tee ${OUTPUT_DIR}/EXP_GA_${mutation}_${mutation_delta}_${crossover}_${bf}_${perform_local_search}.log &
					procs+=( $! )
					if [ ${#procs[@]} -ge $NUM_CONCURRENT_EXPERIMENTS ]; then
						for proc in ${procs[@]}; do
							wait $proc
						done
						procs=()
					fi
				done
			done
		done
	done
done


