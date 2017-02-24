import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;


public class Arvoredec extends Node{
	
    static int classIndex,nLines;
    //representa a àrvore de decisão. O 1º campo diz respeito ao pai do nó 'Node'
    static HashMap<String,Node> tree=new HashMap<String,Node>(); 
    static LinkedList<String> globalAttributes=new LinkedList<String>();

    public static void main(String args[]) throws IOException {
		Scanner in = new Scanner(System.in);
		System.out.print("Formato: ");
		int format=in.nextInt();
		System.out.print("Ficheiro: ");
		String file=in.next();
		System.out.println("............................................");
		//Lê o ficheiro
		LinkedList<String> lineList=readFile(file);
		//guarda atributos numa variavel global
		getAttributes(lineList);
		classIndex=globalAttributes.size()-1;
		//representação da tabela de valores
		String[][] values=getAllValues(lineList);
		//linhas de exemplos
		LinkedList<Integer> examples=new LinkedList<Integer>();
		for(int i=0;i<nLines;i++)
			examples.addLast(i);
		//Lista de atrubutos
		LinkedList<String> attributes=new LinkedList<String>(globalAttributes);
		//remove ID
		attributes.removeFirst();
		//remove CLASS
		attributes.removeLast();
		Node root=new Node();
		root=id3(attributes, values, examples,null,0,null,0);
		if(format==1)
			printTree(root);
		else{
			System.out.print("Ficheiro de exmplos: ");
			String otherFile=in.next();
			//lê o ficheiro
			lineList=new LinkedList<String>();
			lineList=readFile(otherFile);
			lineList.removeFirst();
			String[][] exampleValues=getAllValues(lineList);
			//printin(exampleValues);
			printClasses(exampleValues,root);
		}
		in.close();
		System.out.println("............................................");
    }
    
    
    //algorítmo ID3
    public static Node id3(LinkedList<String> attributes, String[][] values, LinkedList<Integer> examples,String father, int level, String defaultV,int deafultC){
		//sem mais exemplos
		if(examples.isEmpty()){
			//adiciona o valor Majorante da classe anterior- 'defaultV'
			return new Node(level-1,father,defaultV,deafultC,true);
		}
		//todos os elems são da mesma classe
		else if(sameClass(examples,values)){
			//adiciona um novo nó puro
			String classValue = values[examples.getFirst()][classIndex];
			int counter=classCount(examples,values,classValue);
			return new Node(level-1,father,classValue,counter,true);
		}
		//sem mais atributos
		else if(attributes.isEmpty()){
			//adiciona um novo nó - Majorante 
			String classValue=getMajorityValue(examples,values);
			int counter=classCount(examples,values,classValue);
			return new Node(level-1,father,classValue,counter,true);
		}
		//caso geral - recursivo
		else{
	   	    String bestAttribute=getMinEntropyAttribute(attributes,values,examples);
	    	int attributeIndex = globalAttributes.indexOf(bestAttribute);
	    	//nó currente
	    	LinkedList<Node> childs=new LinkedList<Node>();
	   		Node node=new Node(level,bestAttribute,childs,father);
	    	attributes.remove(bestAttribute);

	       	//obtem os valores do atributo que correspondem aos exemplos
	    	LinkedList<String> attributeValues=getValuesFrom(values,examples,attributeIndex);
	   		//Nós filhos
	   		for(String currentValue: attributeValues){
	   			//obtem os exemplos do 'currentValue'
	   			LinkedList<Integer> valuesExamples=getExamplesFrom(values,currentValue, examples, attributeIndex);
	   			//valor majorante
	   			String mv=getMajorityValue(examples,values);
	   			//contagem do majorante
	   			int mc=classCount(examples,values,mv);
	   			childs.add(id3(attributes,values,valuesExamples,currentValue,level+1,mv,mc));
    		}
	    	node.setChilds(childs);
	     	return node;
		}
    }


    //conta o nº de sucessoes de uma classe
    public static int classCount(LinkedList<Integer> examples, String[][] values, String wantedClass){
    	int count=0;
    	for(int i=0;i<nLines;i++)
    		if(examples.contains(i) && values[i][classIndex].equals(wantedClass))
    			count++;
    	return count;
    }


    //retorna classe majorante
    public static String getMajorityValue(LinkedList<Integer> examples, String[][] values){
    	int max=Integer.MIN_VALUE;
    	String majorityClass=null;
    	//relação de uma classe e nº de vezes que ela sucede
		HashMap<String, Integer> classCount=new HashMap<String,Integer>();
		for(int i=0;i<nLines;i++){
	    	//inicializa a contagem
	    	if(examples.contains(i)){
	    		if(!classCount.containsKey(values[i][classIndex]))
					classCount.put(values[i][classIndex], 1);
	    		//atualiza o nº de vezes que um valor sucede
	    		else if(classCount.containsKey(values[i][classIndex])){
					int aux=classCount.get(values[i][classIndex]);
					classCount.put(values[i][classIndex], aux+1);
	    		}
			}
		}
		//descobre a o valor majorante
		for(int i=0;i<nLines;i++)
			if(examples.contains(i) && classCount.get(values[i][classIndex])>max){
				max=classCount.get(values[i][classIndex]);
				majorityClass=values[i][classIndex];
			}
		return majorityClass;
    }

        
    //verifica se todos os exemplos são da mesma classe
    public static boolean sameClass(LinkedList<Integer> examples, String[][] values){
    	String prevClass=values[examples.getFirst()][classIndex];
    	for(int i=0;i<nLines;i++){
    		if(examples.contains(i) && !prevClass.equals(values[i][classIndex]))
    			return false;
    	}
    	return true;
    }
        
    
    //retorna os exmplos correspondentes a um valor - partindo de um conjunto de exemplos
    public static LinkedList<Integer> getExamplesFrom(String[][] values, String wantedValue, LinkedList<Integer> examples, 
    	int attributeIndex){
    	LinkedList<Integer> valuesExamples=new LinkedList<Integer>();
    	for(int i=0;i<nLines;i++)
    		if(examples.contains(i) && values[i][attributeIndex].equals(wantedValue))
    			valuesExamples.add(i);
    	return valuesExamples;
    }
    


    //retorna os valores de um atributo correspondentes aos exemplos
    public static LinkedList<String> getValuesFrom(String[][] values, LinkedList<Integer> examples,int attributeIndex){
    	LinkedList<String> attributeValues=new LinkedList<String>();
    	for(int i=0;i<nLines;i++)
    		if(examples.contains(i) && !attributeValues.contains(values[i][attributeIndex]))
    			attributeValues.add(values[i][attributeIndex]);
    	return attributeValues;
    }
    
    
    //lê o ficheiro 'file' - separando-o por linhas*/
    public static LinkedList<String> readFile(String file) {
		LinkedList<String> lineList = new LinkedList<String>();
		BufferedReader buffer = null;
		try {
	    	String line;
	    	buffer = new BufferedReader(new FileReader(file));
	    	while ((line = (buffer.readLine())) != null)
			lineList.addLast(line);
		} catch (IOException e) {
	    	e.printStackTrace();
		} finally {
	    	try {
			if (buffer != null)
		    	buffer.close();
	    	} catch (IOException e) {
			e.printStackTrace();
	    	}
		}
		return lineList;
    }
    
    
    //retorn um array com todos os atributos (só pode ser chamado uma vez)
    /* define os atributos*/
    public static void getAttributes(LinkedList<String> lineList) {
		String line=lineList.removeFirst();	//Remove a 1ª linha da lista - contém os atributos
		String[] split=line.split(",");	//parsing 
		for(String attribute: split)
			globalAttributes.addLast(attribute);
	}
    
    
    /*define os valores
     *é necessários que o 'getAttributes'  seja chamado antes da chamada deste método
     */
    public static String[][] getAllValues(LinkedList<String> lineList){
		//a primeira linha é feita fora do ciclo apenas para definir o tamanho da tabela de valores
		int i=0,j=0;
		String line=lineList.removeFirst();
		String[] split=line.split(",");
		String[][] values=new String[lineList.size()+1][split.length];
		nLines=lineList.size()+1;
		split=line.split(",");
		for(String value:split){
	    	values[i][j]=value;
	    	j++;
		}
		while(!lineList.isEmpty()){
	    	i++;
	    	j=0;
	    	line=lineList.removeFirst();
	    	split=line.split(",");
	    	for(String value:split){
				values[i][j]=value;
				j++;
		    }
		}
	return values;
    }
     
    
    //retorna a entropia do 'wantedValue'
    public static double getEntropy(String[][] values, String wantedValue, int attributeIndex, LinkedList<Integer> examples){
		//relação de uma classe e nº de vezes que ela sucede
		HashMap<String, Integer> classValues=new HashMap<String,Integer>();
		//nº de vezes que o 'wantedValue' sucede 
		int totalWantedValues=0;
		//adiciona todos valores possiveis da classe associados ao 'wantedValue' e a 'examples' - sem repetições
		for(int i=0;i<nLines;i++){
	 	    if(examples.contains(i)){
	    		//inicializa a contagem
	    		if(!classValues.containsKey(values[i][classIndex]) && values[i][attributeIndex].equals(wantedValue)){
					classValues.put(values[i][classIndex], 1);
					totalWantedValues++;
	    		}
	    		//atualiza o nº de vezes que um valor sucede
	    		else if(classValues.containsKey(values[i][classIndex]) && values[i][attributeIndex].equals(wantedValue)){
					int aux=classValues.get(values[i][classIndex]);
					classValues.put(values[i][classIndex], aux+1);
					totalWantedValues++;	
	    		}
			}
		}
		//guarda os valores da classes cuja probabilidade já foi calculada
		LinkedList<String> checkedClassValues=new LinkedList<String>();
		double entropy=0;
		//cálculo da entropia
		for(int i=0;i<nLines;i++)
			if(examples.contains(i))
		    	if(values[i][attributeIndex].equals(wantedValue) && !checkedClassValues.contains(values[i][classIndex])){
					checkedClassValues.add(values[i][classIndex]);
					//cálculo da probabilidade
					double p=(double) classValues.get(values[i][classIndex])/totalWantedValues;
					//cálculo da entropia
					entropy-=(double) p*(Math.log(p)/Math.log(2));	
		    	}
		return entropy;		
    }
    
    
    //retorna o atributo com a menor entropia
    public static String getMinEntropyAttribute(LinkedList<String> attributes, String[][] values, LinkedList<Integer> examples){
		String bestAttribute=new String();
		double min=(double) Integer.MAX_VALUE;
		for(String attribute: attributes){
	    	int attributeIndex=globalAttributes.indexOf(attribute);
	    	LinkedList<String> checkedValues=new LinkedList<String>();//lista de valores calculados
	    	double entropy=(double) 0;
	    	for(int i=0;i<nLines;i++){
				//cálculo da menor entropia
					if(examples.contains(i) && !checkedValues.contains(values[i][attributeIndex])){
						checkedValues.add(values[i][attributeIndex]);
						double p=getProbability(values[i][attributeIndex],attributeIndex,examples,values);
						entropy+=(double) p*getEntropy(values, values[i][attributeIndex], attributeIndex, examples);
					}
			}			
			if(min>entropy){
				min=entropy;
				bestAttribute=attribute;
			}
		}
		return bestAttribute;
    }

    
  	//calcula a probabilidade de um valor segundo um conjunto de exemplos
  	public static double getProbability(String wantedValue, int attributeIndex, LinkedList<Integer> examples,
  		String[][] values){
  		int c=0;
  		for(int i=0;i<nLines;i++)
  			if(values[i][attributeIndex].equals(wantedValue) && examples.contains(i))
  				c++;
  		return (double) c/examples.size();
  	}


  	//imprime a árvore completa
  	public static void printTree(Node node){
  		//System.out.println();
  		//lista de nós filhos
  		LinkedList<Node> childs=node.getChilds();
  		//nó atributo
  		if(!node.isClass()){
  			//<valor>
  			//espaçamento
  			if (node.getValue() != null){
  				for(int i=0;i<node.getLevel();i++)
  					System.out.print("      ");
   					System.out.println(node.getValue()+": ");}
  			//<atributo>
   			//espaçamento
   			if(node.getValue()!=null)
   				for(int i=0;i<node.getLevel()+1;i++)
  					System.out.print("     ");
  			System.out.println(node.getAttribute());
      		//imprime filhos do atributo
  	  		while(!childs.isEmpty())
  	  			printTree(childs.remove());
  		}
  		//nó puro
  		else{
  			//espaçamento
  			for(int i=0;i<node.getLevel()+1;i++)
  				System.out.print("      ");
   			//<valor>: <classe> (count)
  			System.out.println(node.getAttribute()+": "+node.getValue()+" ("+node.getCount()+")");
   		}
  	}


  	//imprime a classe de todos os valores
  	public static void printClasses(String[][] values, Node root){
  		for(int i=0;i<nLines;i++){
  			Node node=new Node(root);
  			String[] examplesLine=new String[globalAttributes.size()-2];
  			for(int j=1;j<globalAttributes.size()-1;j++)
  				examplesLine[j-1]=values[i][j];
  			getClass(examplesLine,node);
  		}
  	}


  	//imprime a classe de um exemplo
  	public static void getClass(String[] values, Node node){
  		//nó puro
  		if(node.isClass()){
   			System.out.println(node.getValue());
  		}
  		else{
  			LinkedList<Node> childs=node.getChilds();
  			int attributeIndex=globalAttributes.indexOf(node.getAttribute())-1;
  			String attribute=node.getAttribute();
  			String val=node.getValue();
  			while(!childs.isEmpty()){
  				Node child=new Node(childs.remove());
   				if(!child.isClass() && values[attributeIndex].equals(child.getValue()))
  					getClass(values,child);
  				else if(child.isClass() && values[attributeIndex].equals(child.getAttribute()))
  					getClass(values,child);
  			}
  		}
  	}




  	public static void printin(String[][] values){
  		for(String s: globalAttributes)
  			System.out.printf("%-7s",s);
  		System.out.println();
  		for(int i=0;i<nLines;i++){
  			for(int j=0;j<globalAttributes.size();j++)
  				System.out.printf("%-7s",values[i][j]);
  			System.out.println();
  		}
  	}
}

