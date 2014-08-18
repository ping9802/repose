#!/bin/bash

# Expect $1 to be an output file to write to
# any other arguments will just be logged to the output file
OPTIND=1
OUTFILE=$1

# shift off the first argument always, as that's my output file
shift

#TODO: might have to also watch for a shutdown file to exist...

# dump all the rest of the args into this file
echo "ARGS: $@" > ${OUTFILE}

# Dump the environment in there too
echo "ENVIRONMENT:" >> ${OUTFILE}
env >> ${OUTFILE}

# start outputting stuff during the running time period
echo "Some standard out for you!"
echo >&2 "Some standard error also!"

echo "RUNNING OUTPUT...." >> ${OUTFILE}
while true; do
    echo "Running...." >> ${OUTFILE}
    sleep 0.1
done