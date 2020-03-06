package assistant;

import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class SolverAssistant {
	private static SolverAssistant instance = new SolverAssistant();

	private SolverAssistant() {
	}

	public static SolverAssistant getInstance() {
		return instance;
	}
	
	public JavaSymbolSolver getSymbolSolver() {
		
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
//        combinedTypeSolver.add(new JavaParserTypeSolver(Paths.get(path))) ;

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        
        return symbolSolver ;
	}
}