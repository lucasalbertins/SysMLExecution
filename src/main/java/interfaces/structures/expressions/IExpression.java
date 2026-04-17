package interfaces.structures.expressions;

import interfaces.utils.INamedElement;

// OUTDATED
public interface IExpression extends INamedElement {
    // retorna o tipo concreto da Expression (ex: FeatureChainExpression ou LiteralInteger)
	public String getType();
    
    //para uma LiteralExpression, retorna o valor textual (ex: "42" no LiteralInteger, 'texto' no LiteralString...) 
    //default String asLiteral() { return null; } 
    
    // Para cadeias de feature: lista de nomes no chain (ex: ["obj","attr","sub"]) */
    //default List<String> getFeatureChain() { return List.of(); }

	//String asString();
	//String getOperator(); -> demais
	//String getValue(); // ex: integer "1350" ou boolean "false" -> LiteralExpression
	//String getUnit();  // ex: unidade de medida "SI::kg"

	//java.util.List<IExpression> getArguments(); 	
}
