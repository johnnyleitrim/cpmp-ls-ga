#!/bin/sh

CMD="mvn exec:java@iterated-local-search"
LOCAL_SEARCH_PARAMS="-minSearchMoves 1 -maxSearchMoves 2 -maxSearchDuration 1 -bestNeighbourTieBreakingStrategy HIGHEST_CONTAINER"
PERTURBATION_PARAMS="-clearStackSelectionStrategy LOWEST_MIS_OVERLAID_STACK -clearStackStrategy CLEAR_TO_BEST -fillStackAfterClearing false"
PARAMS="-seed 12345 -runs 1 -maxSolutions 10 $LOCAL_SEARCH_PARAMS $PERTURBATION_PARAMS"

NUM_BF_CATEGORIES=32
NUM_CONCURRENT_EXPERIMENTS=3

OUTPUT_DIR="output/ls"

# We use this logic as we want to round up the categories per experiment
# This is to ensure we definitely don't run more concurrent experiments than requested
NUM_CATEGORIES_FOR_ROUNDING=$((NUM_BF_CATEGORIES + NUM_CONCURRENT_EXPERIMENTS - 1))

BF_CATEGORIES_PER_EXPERIMENT=$((NUM_CATEGORIES_FOR_ROUNDING / NUM_CONCURRENT_EXPERIMENTS))

mkdir -p "$OUTPUT_DIR"

current_bf_start=1

while [ $current_bf_start -lt $NUM_BF_CATEGORIES ]; do

  current_bf_end=$((current_bf_start + BF_CATEGORIES_PER_EXPERIMENT - 1))
  if [ $current_bf_end -gt $NUM_BF_CATEGORIES ]; then
    current_bf_end=$NUM_BF_CATEGORIES
  fi

  BF_PARAMS="-bfStart $current_bf_start -bfEnd $current_bf_end"
  $CMD -Dexec.args="$PARAMS $BF_PARAMS" 2>&1 | tee ${OUTPUT_DIR}/EXP_LS_$$_BF_${current_bf_start}_${current_bf_end}.log &
  current_bf_start=$((current_bf_start + BF_CATEGORIES_PER_EXPERIMENT))
done
