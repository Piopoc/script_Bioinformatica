import re

class Node:
    def __init__(self, name=None):
        self.name     = name
        self.children = []
        self.parent   = None
        self.sets     = []
        self.state    = []

def parse_newick(s):
    tokens = re.findall(r'[\(\),;]|[^()\s,;]+', s)
    stack = []
    root  = Node()
    current = root

    for tok in tokens:
        if tok == '(':
            child = Node()
            child.parent = current
            current.children.append(child)
            stack.append(current)
            current = child
        elif tok == ',':
            parent = stack[-1]
            sibling = Node()
            sibling.parent = parent
            parent.children.append(sibling)
            current = sibling
        elif tok == ')':
            current = stack.pop()
        elif tok == ';':
            continue
        else:
            current.name = tok
    while current.parent:
        current = current.parent
    return current

def fitch_pass(root, char_states):
    def postorder(node):
        if not node.children:
            node.sets = [{char_states[node.name]}]
        else:
            for c in node.children:
                postorder(c)
            left, right = node.children
            A, B = left.sets[-1], right.sets[-1]
            inter = A & B
            node.sets.append(inter if inter else A | B)
        node.sets = node.sets
    postorder(root)

    def preorder(node, parent_state=None):
        if parent_state is None:
            node.state.append(next(iter(node.sets[-1])))
        else:
            if parent_state in node.sets[-1]:
                node.state.append(parent_state)
            else:
                node.state.append(next(iter(node.sets[-1])))
        for c in node.children:
            preorder(c, node.state[-1])
    preorder(root)

def run_fitch_multi(root, leaf_states):
    m = len(next(iter(leaf_states.values())))
    for n in traverse(root):
        n.sets = []
        n.state = []
    for i in range(m):
        char_states = {name: states[i] for name, states in leaf_states.items()}
        fitch_pass(root, char_states)

def traverse(node):
    yield node
    for c in node.children:
        yield from traverse(c)

def compute_fitch_cost(root):
    """Calcola quante mutazioni sono avvenute dal genitore ai figli."""
    cost = 0
    for node in traverse(root):
        if node.parent is not None:
            for i in range(len(node.state)):
                if node.state[i] != node.parent.state[i]:
                    cost += 1
    return cost

if __name__ == '__main__':
    newick = input("Inserisci l'albero Newick: ").strip()
    states_raw = input("Inserisci gli stati (es. 'A:ABC, B:ACC'): ").strip()
    root = parse_newick(newick)
    leaf_states = {}
    for pair in states_raw.split(','):
        name, seq = pair.split(':')
        leaf_states[name.strip()] = seq.strip()
    run_fitch_multi(root, leaf_states)
    mut_cost = compute_fitch_cost(root)
    print("Costo di mutazione minimo:", mut_cost)

# Esempio di input fatto in classe:
# (((01,02),(03,04)),05)
# 01:CA, 02:GA, 03:CC, 04:AC, 05:AT