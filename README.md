# MinesweeperFX
Minesweeper Solving Tool Simulator in JavaFX

Strives to maximize the solving rate by deterministic algorithms and compares the quality of various guessing heuristics.

Requires javafx-jdk 15.0.1 or higher.

## Currently Implemented

### Subset rule

Commonly known as the 1-1 or 1-2 rules.

Let `x` be a sqaure on the board which is revealed and has hidden unflagged neighboring squares. `x` provides the following clue:

`cx = ({x1, .. , xn}, bx)` with `n` being the number of hidden unflagged neighbors and `b` the number of unflagged bombs in neighboring squares.

Let `cy = ({y1, .. , ym}, by)` be another clue and `cz` a non-empty intersection of `{x1, .. , xn}` and `{y1, .. , ym}` with `bc = 0` for now.

`cz` has a minimum number of bombs that have be contained and a maximum number of bombs that may be contained within the squares. Those are determined as follows:

`minBombs = max( bx - (|cx| - |cz|), by - (|cy| - |cz|) )`

E.g. if `cx` has 4 squares, contains 3 bombs and `cz` has 2 squares,
a maximum of 2 bombs may be contained in the remaining 2 non-intersecting squares of `cx` and at least 1 bomb has to be in the intersection cz.
The same applies to `cy` and the maximum of the two potential values is the minimum number of bombs in the intersection.

`maxBombs = min( bx, by, |cz| )`

The maximum number of bombs possible in the intersection `cz` is the mandatory minimum number of bombs in `cx`, `cy` or naturally the size of the intersection itself.

If minBombs equals maxBombs, the number of bombs in the intersection has to be just that. As a consequence, the clues `cx`, `cy` can be split into `cx'`, `cy'` and `cz`
where `cx'` is `cx` minus the squares in `cz` and minus the bombs in `cz`. The same applies to `cy'`.

In case of `cx == cz`, `cx` may be deleted since all its information is now in `cz`. The same applies to `cy`.

After intersecting enough clues it may happen that `cx'` contains 0 bombs, in which case every square in `cx'` may be safely revealed
or that `cx'` contains as many bombs as it has squares, in which case every square may be flagged as bomb.

This chaining and unravelling of clues gradually reveals the entire board in the best case, which currently occurs about 9.8% of the time on the standard hard board.
Flagging is not strictly necessary for this rule, because it reveals no new information, but it helps with performance.

## Coming up

- extending the subset rule to cover 1-3-1 corners
- bottom-up solving algorithm based on remaining bombs
- extensively multithreading the simulations
- determining the optimal starting square
- all of the guessing heuristics
- proper UI with fancy animations and all
- proper documentation with report of findings
