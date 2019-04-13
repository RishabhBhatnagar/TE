from string import ascii_uppercase


class Node:
    def __init__(self, name):
        self.name = name
        self.pr = 1
        self.inbound_links = set()
        self.outbound_links = set()
    def add_outbound_link(self, node_obj):
        assert isinstance(node_obj, Node)
        self.outbound_links.add(node_obj)
        node_obj.inbound_links.add(self)
    def __repr__(self):
        return self.name


class Graph:
    def __init__(self, adj_matrix):
        self.graph = {}
        self.history = []
        self.epochs = 0
        
        for name in ascii_uppercase[:len(adj_matrix)]: 
            self.graph[name] = Node(name)

        for i, row in enumerate(adj_matrix):
            for j, cell in enumerate(row):
                i_name, j_name = ascii_uppercase[i], ascii_uppercase[j]
                if cell == 1 and i != j:
                    self.graph[i_name].add_outbound_link(self.graph[j_name])

        while True:
            self.run_page_rank()
            self.history.append(list(self.get_all_prs()))
            if len(self.history) > 1:
                if self.history[-1] == self.history[-2]:
                    break
            self.epochs += 1
        print(self.epochs, "epochs required to converge.")
        print("Printing some iterations: ")
        for i in range(1, self.epochs, self.epochs):
            print("Epoch number:", i)
            for j, pr in enumerate(self.history[i]):
                print("PR({}) = {}".format(ascii_uppercase[j], pr))
            print()
            input("\n")

    def get_all_prs(self):
        for name in ascii_uppercase[:len(self.graph.keys())]:
            node = self.graph[name]
            yield node.pr

    def run_page_rank(self, d=.85):
        for name in ascii_uppercase[:len(self.graph.keys())]:
            node = self.graph[name]
            print(name, node.pr)
            print([(ibl, ibl.pr, ibl.outbound_links, ) for ibl in node.inbound_links])
            node.pr = (1-d) + d*sum(ibl.pr/len(ibl.outbound_links) for ibl in node.inbound_links)
        for name in ascii_uppercase[:len(self.graph.keys())]:
            print(self.graph[name], self.graph[name].pr)

        input("\n")

        for name in ascii_uppercase[:len(self.graph.keys())]:
            node = self.graph[name]

Graph([[0, 1, 1], [0, 0, 1], [1, 0, 0]])

