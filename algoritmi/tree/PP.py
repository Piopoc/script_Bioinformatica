import numpy as np
import networkx as nx
import matplotlib.pyplot as plt

def draw_tree(nodes):
    G = nx.DiGraph()
    for node, data in nodes.items():
        if node == 0:
            full_label = "Root"
        else:
            matrix_row = data.get("matrix_row", "")
            label = data.get("label", "")
            if matrix_row:
                full_label = f"{matrix_row}\n{label}"
            else:
                full_label = label
        G.add_node(node, label=full_label)
    for parent, data in nodes.items():
        for child in data["children"]:
            edge_lab = nodes[child].get("edge_label", "")
            if not edge_lab or not edge_lab.isdigit():
                edge_lab = "0"
            G.add_edge(parent, child, label=edge_lab)
    pos = nx.nx_pydot.graphviz_layout(G, prog="dot")
    node_labels = nx.get_node_attributes(G, "label")
    edge_labels = nx.get_edge_attributes(G, "label")
    nx.draw(G, pos, with_labels=True, labels=node_labels, node_size=1500, node_color="lightblue", arrows=True)
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_color="red")
    plt.title("Perfect Phylogeny")
    plt.show()

def check_inclusion_disjointness(M):
    print("1. Controllo proprietà di inclusione/disgiunzione:")
    col_sums = np.sum(M, axis=0)
    sorted_indices = np.argsort(-col_sums)
    M_sorted = M[:, sorted_indices]
    print("Matrice ordinata per colonne (decrescente per numero di 1):\n", M_sorted)

    M_ext = np.hstack([np.ones((M.shape[0], 1), dtype=int), M_sorted])
    print("Aggiunta colonna 0 di soli 1:\n", M_ext)

    pointers = np.full_like(M_ext, -1)
    for j in range(1, M_ext.shape[1]):
        for i in range(M_ext.shape[0]):
            if M_ext[i, j] == 1:
                for k in range(j-1, -1, -1):
                    if M_ext[i, k] == 1:
                        pointers[i, j] = k
                        break
    print("Puntatori (ogni 1 punta al precedente 1 nella riga):\n", pointers)

    for j in range(1, M_ext.shape[1]):
        parents = set()
        for i in range(M_ext.shape[0]):
            if M_ext[i, j] == 1:
                parents.add(pointers[i, j])
        if len(parents) > 1:
            print(f"Colonna {j}: i puntatori immediati sono {parents} -> NO Perfect Phylogeny")
            return False, None, sorted_indices, M_sorted
        print(f"Colonna {j}: il puntatore immediato è {parents} -> OK")
    print("La matrice ha la proprietà di inclusione/disgiunzione: esiste una Perfect Phylogeny.")
    return True, pointers, sorted_indices, M_sorted

def build_phylogeny(M, pointers, sorted_indices, taxa_labels, char_labels):
    print("\n2. Costruzione dell'albero filogenetico (Perfect Phylogeny):")
    n_taxa, n_char = M.shape
    nodes = {j: {"children": [], "taxa": set()} for j in range(n_char+1)}

    for j in range(1, n_char+1):
        parents = set()
        for i in range(n_taxa):
            if M[i, j-1] == 1:
                k = pointers[i, j]
                if k < j:
                    parents.add(k)
        for p in parents:
            nodes[p]["children"].append(j)
        nodes[j]["num"] = char_labels[ sorted_indices[j-1] ]
        nodes[j]["edge_label"] = nodes[j]["num"]
        print(f"Colonna {j}: archi dai genitori {parents} al nodo {j} con num={nodes[j]['num']}")

    for i in range(n_taxa):
        last_one = 0
        for j in range(n_char, 0, -1):
            if M[i, j-1] == 1:
                last_one = j
                break
        nodes[last_one]["taxa"].add(taxa_labels[i])

    next_id = max(nodes.keys()) + 1
    def get_next_id():
        nonlocal next_id
        nid = next_id
        next_id += 1
        return nid


    used_chars = set()

    inv_sorted = np.argsort(sorted_indices)

    def process_labels(u):
        for child in nodes[u]["children"]:
            process_labels(child)
        if u == 0:
            nodes[u]["label"] = "Root"
            return

        if len(nodes[u]["children"]) == 0:
            if "num" in nodes[u]:
                col = u - 1
                new_chars = []
                for i in range(n_taxa):
                    if M[i, col] == 1 and taxa_labels[i] not in used_chars:
                        new_chars.append(taxa_labels[i])
                nodes[u]["label"] = ",".join(new_chars)
            else:
                nodes[u]["label"] = ",".join(nodes[u]["taxa"])
        else:
            if "num" in nodes[u]:
                col = u - 1
                new_chars = []
                for i in range(n_taxa):
                    if M[i, col] == 1 and taxa_labels[i] not in used_chars:
                        new_chars.append(taxa_labels[i])
                nodes[u]["label"] = ",".join(new_chars)
            else:
                nodes[u]["label"] = ",".join(nodes[u]["taxa"])

        current_letters = [c for c in nodes[u]["label"].split(",") if c]
        if current_letters:
            rows = []
            for letter in current_letters:
                if letter in taxa_labels:
                    idx = taxa_labels.index(letter)
                    orig_row = M[idx, :][inv_sorted]
                    row_str = " ".join(map(str, orig_row))
                    rows.append(row_str)
            if rows:
                nodes[u]["matrix_row"] = "\n".join(rows)

        current_chars = [c for c in nodes[u]["label"].split(",") if c]
        if len(nodes[u]["children"]) == 0 and len(current_chars) == 1:
            used_chars.add(current_chars[0])
        else:
            for o in current_chars[:]:
                if o in used_chars:
                    current_chars = [c for c in current_chars if c != o]
                    continue
                curr = [c for c in current_chars if c != o]
                nodes[u]["label"] = ",".join(curr)
                new_leaf_id = get_next_id()
                nodes[new_leaf_id] = {"children": [], "taxa": set(), "label": o, "edge_label": ""}
                if o in taxa_labels:
                    idx = taxa_labels.index(o)
                    orig_row = M[idx, :][inv_sorted]
                    row_str = " ".join(map(str, orig_row))
                    nodes[new_leaf_id]["matrix_row"] = row_str
                nodes[u]["children"].append(new_leaf_id)
                used_chars.add(o)
                current_chars = [c for c in nodes[u]["label"].split(",") if c]

    process_labels(0)

    def print_tree(node, prefix="", incoming_edge=""):
        if node == 0:
            lab = "Root"
        else:
            lab = nodes[node]["label"]
        out_line = prefix
        if incoming_edge:
            out_line += f"--({nodes[node].get('edge_label', '0')})--> "
        out_line += f"{lab}"
        print(out_line)
        for child in nodes[node]["children"]:
            print_tree(child, prefix + "  ", nodes[child].get("edge_label", "0"))
        if nodes[node]["taxa"] and not nodes[node]["children"]:
            for t in nodes[node]["taxa"]:
                print(prefix + "  " + f"[{t}]")
                    
    print("\nAlbero Perfect Phylogeny (struttura):")
    print_tree(0)
    draw_tree(nodes)

def main():
    taxa_labels = ["A", "B", "C", "D", "E"] # righe
    char_labels = ["1", "2", "3", "4", "5"] # colonne
    M = np.array([
        [1, 1, 0, 0, 0],
        [0, 0, 1, 0, 0],
        [1, 1, 0, 0, 1],
        [0, 0, 1, 1, 0],
        [0, 1, 0, 0, 0]
    ])
    print("Matrice originale:\n", M)
    ok, pointers, sorted_indices, M_sorted = check_inclusion_disjointness(M)
    if ok:
        build_phylogeny(M_sorted, pointers, sorted_indices, taxa_labels, char_labels)

if __name__ == "__main__":
    main()