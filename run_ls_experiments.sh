#!/bin/sh

CMD="mvn exec:java@iterated-local-search-algorithm -runs 10 -maxSearchDuration 1 -perturbation LOWEST_MISOVERLAID_STACK_CLEARING -lowerBound BF -execute"

NEIGHBOURHOODS="1-2"
NUM_BF_CATEGORIES=32
NUM_CONCURRENT_EXPERIMENTS=3

OUTPUT_DIR="output/ls"

# We use this logic as we want to round up the categories per experiment
# This is to ensure we definitely don't run more concurrent experiments than requested
NUM_CATEGORIES_FOR_ROUNDING=$((NUM_BF_CATEGORIES + NUM_CONCURRENT_EXPERIMENTS - 1))

BF_CATEGORIES_PER_EXPERIMENT=$((NUM_CATEGORIES_FOR_ROUNDING / NUM_CONCURRENT_EXPERIMENTS))

mkdir -p "$OUTPUT_DIR"

for neighbourhood in $NEIGHBOURHOODS; do
	current_bf_start=1

	while [ $current_bf_start -lt $NUM_BF_CATEGORIES ]; do

		current_bf_end=$((current_bf_start + BF_CATEGORIES_PER_EXPERIMENT - 1))
		if [ $current_bf_end -gt $NUM_BF_CATEGORIES ]; then
			current_bf_end=$NUM_BF_CATEGORIES
		fi

		BF_PARAMS="-bfStart $current_bf_start -bfEnd $current_bf_end"
		$CMD -neighbourhoods $neighbourhood $BF_PARAMS | tee ${OUTPUT_DIR}/EXP_LS_LOWEST_MISOVERLAID_STACK_CLEARING_MOVES_${neighbourhood}_BF_${current_bf_start}_${current_bf_end}.log &
		current_bf_start=$((current_bf_start + BF_CATEGORIES_PER_EXPERIMENT))
	done
done
