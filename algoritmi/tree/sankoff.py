import math

INF = float('inf')

# -----------------------------
# Strutture dati e costi
# -----------------------------
class Node:
    def __init__(self, id, observed=None):
        self.id = id
        self.children = []
        self.observed = observed
        self.state = None

# Insieme degli stati S
S = ['A', 'C', 'G', 'T']

cost = [
    [0, 2.5, 1, 2.5],
    [2.5, 0, 2.5, 1],
    [1, 2.5, 0, 2.5],
    [2.5, 1, 2.5, 0]
]

dp = {}

# -----------------------------
# Funzioni ausiliarie
# -----------------------------
def post_order(node, order):
    for child in node.children:
        post_order(child, order)
    order.append(node)

def backtrack(node, chosen_state):
    node.state = chosen_state
    print(f"Backtracking: Nodo {node.id} assegna stato {chosen_state}")
    for child in node.children:
        best_state = None
        best_cost = INF
        for t in S:
            curr_cost = dp[child.id][t] + cost[S.index(chosen_state)][S.index(t)]
            print(f"  Nodo {child.id}: costo se stato figlio = {t} con costo transizione {cost[S.index(chosen_state)][S.index(t)]}: {dp[child.id][t]} + {cost[S.index(chosen_state)][S.index(t)]} = {curr_cost}")
            if curr_cost < best_cost:
                best_cost = curr_cost
                best_state = t
        print(f"  Nodo {child.id} sceglie stato {best_state} (costo = {best_cost})")
        backtrack(child, best_state)

# -----------------------------
# Funzione principale: sankoff
# -----------------------------
def sankoff(root):
    order = []
    post_order(root, order)
    print("Ordine in post-ordine:", [node.id for node in order])
    
    for node in order:
        dp[node.id] = {s: INF for s in S}
        if not node.children:
            print(f"Inizializzazione DP foglia Nodo {node.id}: stato osservato = {node.observed}")
            for s in S:
                dp[node.id][s] = 0 if s == node.observed else INF
            print(f"  dp[{node.id}] = {dp[node.id]}")
    
    for node in order:
        if node.children:
            print(f"Calcolo DP per Nodo interno {node.id}")
            for s in S:
                total = 0

                for child in node.children:
                    min_cost = INF
                    for t in S:
                        candidate = dp[child.id][t] + cost[S.index(s)][S.index(t)]
                        if candidate < min_cost:
                            min_cost = candidate
                    print(f"  Per Nodo {node.id} se assume stato {s}, costo minimo dal figlio {child.id} = {min_cost}")
                    total += min_cost
                dp[node.id][s] = total
            print(f"  dp[{node.id}] = {dp[node.id]}")
                
    root_cost = min(dp[root.id].values())
    best_root_state = None
    for s in S:
        if dp[root.id][s] == root_cost:
            best_root_state = s
            break
    print(f"Alla radice (Nodo {root.id}) dp = {dp[root.id]}, costo ottimo = {root_cost} con stato {best_root_state}")
        
    backtrack(root, best_root_state)
    
    return root_cost

if __name__ == "__main__":

    # Per gli esercizi futuri:

    # 1. modificare la tabella delle mutazioni all'inizio
    # 2. modificare il numero di nodi totale
    # 3. definire i nodi e la struttura dell'albero
    # 4. definire lo stato delle foglie

    nodes={}
    nm_nodes = 9
    for i in range(nm_nodes):
        nodes[i] = Node(i)
    nodes[0].children = [nodes[1], nodes[2]]
    nodes[1].children = [nodes[5], nodes[8]]
    nodes[2].children = [nodes[3], nodes[4]]
    nodes[5].children = [nodes[6], nodes[7]]

    nodes[3].observed = 'A'
    nodes[4].observed = 'C'
    nodes[8].observed = 'C'
    nodes[6].observed = 'G'
    nodes[7].observed = 'A'

    opt_cost = sankoff(nodes[0])
    print("\nCosto ottimo:", opt_cost)
    print("\nStati ricostruiti:")
    for i in range(nm_nodes):
        print(f" Nodo {nodes[i].id}: stato = {nodes[i].state}")