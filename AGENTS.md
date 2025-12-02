# Agent Guide for Project 3: Hadoop MapReduce

This guide provides essential information for agents working in this Hadoop MapReduce project for CSE 4331. The project consists of two separate MapReduce programs: a main movie/actor analysis job and a classic WordCount example.

## Project Overview

- **Primary Project**: Two‑stage MapReduce pipeline that joins IMDb title basics with actors and counts movies per year for a specific actor (`Christian Bale`).
- **Secondary Project**: Standard WordCount example (`WordCountProgram/`).
- **Environment**: Designed for the Expanse supercomputer (SDSC) with SLURM workload manager, Hadoop 3.2.2, and Java.
- **Build System**: Bash scripts that compile Java sources with `hadoop classpath` and package into JARs.
- **No Maven/Gradle**: All dependencies are provided by the Hadoop module.

## Essential Commands

### Building the Main Project

```bash
cd src
./years.count.build
```

This script:
- Loads required modules (`cpu/0.15.4 gcc/7.5.0 openjdk hadoop/3.2.2`)
- Sets `HADOOP_HOME` and `PATH`
- Compiles all Java sources in `org/dtb9096/hadoop/`
- Creates `Project3.jar` in the `src/` directory

### Running the Main Project

**Local (single‑node) mode** (for testing on a login node):

```bash
cd src
./wordcount.local.run
```

**Distributed (multi‑node) mode** (requires SLURM allocation):

```bash
cd src
sbatch year.counts.distr1.run
```

Two distributed scripts are available with different mapper/reducer counts:

- `year.counts.distr1.run`: uses 2 mappers and 1 reducer for job1, 1 mapper and 1 reducer for job2
- `year.counts.distr2run`: uses 2 mappers and 2 reducers for both jobs

Both scripts:
- Allocate 1 node with 4 tasks per node
- Start Hadoop HDFS with `myhadoop‑configure.sh`
- Upload input files (`../imdb00.title.basics.tsv` and `../imdb00.title.actors.csv`) to HDFS
- Run `Project3.jar` with class `org.dtb9096.hadoop.Main` (which now accepts four additional parameters for mapper/reducer counts)
- Retrieve results to `output‑distr/`

### Building the WordCount Example

```bash
cd WordCountProgram
./wordcount.build
```

Creates `WordCount.jar` from `WordCount.java`.

### Running the WordCount Example

**Local mode**:

```bash
cd WordCountProgram
./wordcount.local.run
```

**Distributed mode**:

```bash
cd WordCountProgram
sbatch wordcount.dist.run
```

The WordCount distributed script uploads `wc‑input.txt` to HDFS and writes output to `output‑distr/`.

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
│   ├── years.count.build                   # Build script for main project
│   ├── wordcount.local.run               # Local run script (SLURM)
│   └── year.counts.distr1.run, year.counts.distr2run  # Distributed run scripts (SLURM)
├── WordCountProgram/
│   ├── WordCount.java                    # Classic WordCount
│   ├── wc‑input.txt                      # Example input
│   ├── wordcount.build                   # Build script
│   ├── wordcount.local.run               # Local run script
│   └── wordcount.dist.run                # Distributed run script
├── .gitignore                            # Standard Java ignores
└── project3.iml                          # IntelliJ project file
```

## Code Conventions

- **Package naming**: `org.dtb9096.hadoop`
- **Mapper/Reducer structure**: Each class is a single Java file with a clear `map`/`reduce` method.
- **Assertions**: Several mappers use `assert` statements to validate token counts (e.g., `assert itr.countTokens() == 5`). These are enabled by default in the Hadoop environment.
- **Hard‑coded constants**:
  - `ActorFilterMapper.ACTOR = "Christian Bale"` – the actor to count.
  - `TextOutputFormat.SEPARATOR = "\t"` in `Main.java`.
  - Mapper/reducer counts are now configurable via command-line arguments (four additional parameters after input/output paths).
- **Intermediate output**: Job1 writes to `temp.txt` (relative path) which is automatically used as input for Job2.
- **Key/Value types**:
  - Job1: `Text` keys and values.
  - Job2: `Text` keys, `IntWritable` values.

## Gotchas & Important Notes

1. **Input files must be placed in the parent directory** (`../`):
   - `imdb00.title.basics.tsv`
   - `imdb00.title.actors.csv`
   The distributed scripts expect these files to exist one level above the project root.

2. **Environment‑specific paths**:
   - Scripts assume Hadoop is installed under `/expanse/lustre/projects/uot182/fegaras/hadoop‑3.2.2`.
   - `module load cpu/0.15.4 gcc/7.5.0 openjdk hadoop/3.2.2` is required before building or running.
   - The `MYHADOOP_HOME` variable points to a custom myhadoop installation.

3. **SLURM parameters**:
   - Local scripts request 1 node, 1 task, 5 GB memory, 10‑second walltime.
   - Distributed scripts request 1 node, 4 tasks, 16 GB memory, 29‑second walltime.

4. **Temporary directories**:
   - Hadoop temporary data is written to `/tmp/hadoop` (local) and `/scratch/$USER/job_$SLURM_JOBID` (distributed).
   - The local script deletes `/tmp/hadoop` after completion to avoid conflicts.

5. **No unit tests** – the project relies on manual verification with the provided input files.

6. **The WordCount example is independent** – it uses its own JAR and input file.

## Dependencies

- **Java** (OpenJDK)
- **Hadoop 3.2.2**
- **Apache Commons Text** (used in `BasicActorJoinReducer` for `StringTokenizer` – note the import is `org.apache.commons.text.StringTokenizer`, not the standard Java version)
- **SLURM** (for distributed execution)
- **myhadoop** (custom Hadoop configuration for Expanse)

## Typical Workflow for Agents

1. **Check input files**: Ensure `../imdb00.title.basics.tsv` and `../imdb00.title.actors.csv` exist (or provide your own).
2. **Build**: Run the appropriate `wordcount.build` script.
3. **Test locally**: Run the local script to verify the pipeline works.
4. **Run distributed**: Submit the distributed script via `sbatch` and monitor the output file (e.g., `project3.distr.out`).
5. **Inspect results**: Output is written to `output‑distr/` (distributed) or `output‑wc‑local` (local WordCount).

## References

- `instructions.pdf` – assignment description.
- `expanse_enrollment.pdf` – Expanse cluster setup guide.
- `Access_and_Expanse_setup_v1.docx` – additional cluster documentation.

---

*Generated for agents working with Hadoop MapReduce on the Expanse cluster.*