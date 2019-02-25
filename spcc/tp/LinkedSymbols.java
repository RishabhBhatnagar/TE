class SymbolNode{
    Symbol symbol;
    SymbolNode next;
    SymbolNode prev;

    SymbolNode(Symbol symbol){
        this.symbol = symbol;
        this.next = null;
    }
}
class LinkedSymbols {
    SymbolNode start = null;
    SymbolNode current = null;
    int count = 0;
    LinkedSymbols(){
        this.start = null;
        this.current = null;
    }
    void append(Symbol symbol){
        this.count += 1;
        if(null == this.start){
            this.start = new SymbolNode(symbol);
            this.current = start;
        } else{
            SymbolNode node = new SymbolNode(symbol);
            node.prev = this.current;
            this.current.next = node;
            this.current = node;
        }
    }
    Symbol get(int index){
        if(index >= this.count){
            throw new IndexOutOfBoundsException("Trying to access element which is not in linked list.");
        } else{
            SymbolNode current = this.start;
            int i = 0;
            while (i != index){
                i += 1;
                current = current.next;
            }
            return current.symbol;
        }
    }
    String getPrintable(){
        String op = "";
        SymbolNode current = this.start;
        int i = 0;
        while (i != this.count){
            i += 1;
            op += current.symbol.data;
            current = current.next;
        }
        return op;
    }
}