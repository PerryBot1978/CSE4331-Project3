# CSE 4331 Project 3: Hadoop MapReduce Movie/Actor Analysis

A two-stage Hadoop MapReduce pipeline that joins IMDb title basics with actors, filters for a specific actor (Christian Bale), and counts movies per year.

## Project Overview

This project processes two IMDb datasets:
- **Title Basics** (`imdb00.title.basics.tsv`): movie/TV show metadata
- **Title Actors** (`imdb00.title.actors.csv`): actor information for each title

The pipeline performs:
1. **Job 1**: Join movies with actors, filtering for movies/tvMovies
2. **Job 2**: Filter for "Christian Bale" and count movies per year

## Prerequisites

- **Environment**: Expanse supercomputer (SDSC) with SLURM workload manager
- **Software**: Hadoop 3.2.2, OpenJDK, GCC 7.5.0
- **Input Files**: Place `imdb00.title.basics.tsv` and `imdb00.title.actors.csv` in the parent directory (`../`)

## Project Structure

```
.
├── src/
│   ├── org/dtb9096/hadoop/
│   │   ├── Main.java                     # Driver for two‑stage job
│   │   ├── job1/
│   │   │   ├── TitleBasicsMapper.java    # Filters movies/tvMovies
│   │   │   ├── TitleActorsMapper.java    # Parses actor data
│   │   │   └── BasicActorJoinReducer.java # Joins movie with actors
│   │   └── job2/
│   │       ├── ActorFilterMapper.java    # Filters for “Christian Bale”
│   │       └── TitleCountReducer.java    # Counts movies per year
│   ├── wordcount.build                   # Build script for main project
│   ├── wordcount.local.run               # Local run script (SLURM)
│   └── wordcount.distr.run               # Distributed run script (SLURM)
└── README.md
```

## Building

```bash
cd src
./wordcount.build
```

This script:
- Loads required modules (`cpu/0.15.4 gcc/7.5.0 openjdk hadoop/3.2.2`)
- Sets `HADOOP_HOME` and `PATH`
- Compiles all Java sources
- Creates `Project3.jar` in the `src/` directory

## Running

### Local (Single‑Node) Mode
For testing on a login node (limited to small datasets):

```bash
cd src
sbatch wordcount.local.run
```

The script requests 1 node, 1 task, 5 GB memory, and 10‑second walltime.

### Distributed (Multi‑Node) Mode
For production runs on the compute partition:

```bash
cd src
sbatch wordcount.distr.run
```

The script:
- Allocates 1 node with 4 tasks per node, 16 GB memory, 29‑second walltime
- Starts Hadoop HDFS with `myhadoop‑configure.sh`
- Uploads input files to HDFS
- Runs `Project3.jar` with class `org.dtb9096.hadoop.Main`
- Retrieves results to `output‑distr/`

## Output Format

The final output is written to `output‑distr/year.counts.txt/part‑r‑00000` with tab‑separated values:

```
<year>  <count>
```

Each line shows how many movies Christian Bale starred in that year.

## Example Output

```
1987    1
1992    2
2005    3
...
```

## Notes

- The WordCount example program (`WordCountProgram/`) is maintained locally but excluded from the repository (see `.gitignore`).
- Intermediate output from Job1 is written to `temp.txt` (relative path) and automatically used as input for Job2.
- The local script deletes `/tmp/hadoop` after completion to avoid conflicts.
- Assertions are enabled by default in the Hadoop environment.

## References

- `instructions.pdf` – assignment description
- `expanse_enrollment.pdf` – Expanse cluster setup guide
- `Access_and_Expanse_setup_v1.docx` – additional cluster documentation
- `AGENTS.md` – detailed technical guide for agents