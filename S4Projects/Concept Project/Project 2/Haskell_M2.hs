import System.Random
import Test.HUnit

data PriorityQueue = PQ PQNode PriorityQueue | Empty deriving (Show, Eq) 
data PQNode = Node Int Int deriving (Show, Eq)


-- This is a function to generate x random numbers, using a seeded generator, in the closed range [0,9].
getRandomNumbers :: Int -> Int -> [Int]
getRandomNumbers x seed = 
	let g = mkStdGen seed
	in take x (randomRs (0, 9) g)

-- This is a function to format the 2D list nicely in the terminal.
printRailsNetwork :: [[Int]] -> IO ()
printRailsNetwork network = mapM_ print network



-- PART A -- Beginning of railsNetwork --
railsNetwork :: Int -> Int -> [[Int]]
railsNetwork n seed = buildMatrix n (getRandomNumbers (n*n) seed) 1

buildMatrix :: Int -> [Int] -> Int -> [[Int]]
buildMatrix _ [] _ = []
buildMatrix n randList rowIndex = buildRow n randList rowIndex 1 : buildMatrix n (drop n randList) (rowIndex+1)

buildRow :: Int -> [Int] -> Int -> Int -> [Int]
buildRow 0 _ _ _ = []
buildRow count (h:t) row col = (if row == col then 0 else h) : buildRow (count-1) t row (col+1)
-- End of railsNetwork --



-- PART B -- Beginning of pullLever --

-- End of pullLever --



-- PART C -- Beginning of initializeSource --
initializeSource :: Int -> [[Int]] -> PriorityQueue
initializeSource source adjMatrix = buildQ 1
   where
    buildQ i
      | i > length adjMatrix = Empty
      | i == source = insert (Node i 0) (buildQ (i + 1))
      | otherwise   = insert (Node i 9999) (buildQ (i + 1))

insert :: PQNode -> PriorityQueue -> PriorityQueue
insert newNode Empty = PQ newNode Empty
insert (Node sK sP) (PQ (Node cK cP) rest)
    | sP <= cP = PQ (Node sK sP) (PQ (Node cK cP) rest)
    | otherwise = PQ (Node cK cP) (insert (Node sK sP) rest) 
-- End of initializeSource --



-- PART D -- Beginning of computeShortestPathCost --

-- End of computeShortestPathCost --





