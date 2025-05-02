import matplotlib.pyplot as plt
import numpy as np

def print_grid(x, y, dp):
    n = len(x)
    m = len(y)
    print("       " + "    ".join(y))
    print("   " + " ".join(f"{dp[0][j]:3d}" for j in range(m+1)))
    for i in range(1, n+1):
        print(f"{x[i-1]}  ", end="")
        print(" ".join(f"{dp[i][j]:3d}" for j in range(m+1)))

def backtrack_path(backtrack, x, y):
    i, j = len(x), len(y)
    path = [(i, j)]
    while i > 0 or j > 0:
        cell_moves = backtrack[i][j]
        if i > 0 and j > 0 and ('diag' in cell_moves):
            i -= 1
            j -= 1
        elif i > 0 and ('up' in cell_moves):
            i -= 1
        elif j > 0 and ('left' in cell_moves):
            j -= 1
        else:
            if i > 0:
                i -= 1
            else:
                j -= 1
        path.append((i, j))
    return list(reversed(path))

def plot_alignment(x, y, dp, main_path, all_paths):
    n = len(x)
    m = len(y)
    dp_arr = np.array(dp)
    
    fig, ax = plt.subplots(figsize=(8,6))
    
    ax.imshow(dp_arr, cmap="Greys", origin="upper")
    
    ax.set_xticks(np.arange(-0.5, m+0.5, 1), minor=True)
    ax.set_yticks(np.arange(-0.5, n+0.5, 1), minor=True)
    ax.grid(which="minor", color="black", linestyle='-', linewidth=1)
    
    ax.set_xticks(np.arange(0, m, 1)+0.495)
    ax.set_yticks(np.arange(0, n, 1)+0.495)
    ax.set_xticklabels(list(y), fontsize=12, color='blue')
    ax.set_yticklabels(list(x), fontsize=12, color='blue')
    ax.xaxis.tick_top()
    ax.tick_params(axis="both", which="both", length=0)
    
    inter_x = np.arange(-0.5, m+0.5, 1)
    inter_y = np.arange(-0.5, n+0.5, 1)
    xx, yy = np.meshgrid(inter_x, inter_y)
    ax.scatter(xx, yy, c='black', s=3, zorder=5)
    
    for p in all_paths:
        if p != main_path:
            path_i = [pt[0] for pt in p]
            path_j = [pt[1] for pt in p]
            ax.plot(np.array(path_j) - 0.5, np.array(path_i) - 0.5, marker="o",
                    color="green", linewidth=2)
    
    path_i = [pt[0] for pt in main_path]
    path_j = [pt[1] for pt in main_path]
    ax.plot(np.array(path_j) - 0.5, np.array(path_i) - 0.5, marker="o",
            color="red", linewidth=2)
    
    ax.set_title("Optimal Alignment Track (LCS via Manhattan Tourist)", pad=30)
    plt.show()

def lcs_dp_multi(x, y):
    n = len(x)
    m = len(y)
    dp = [[0]*(m+1) for _ in range(n+1)]
    backtrack = [[[] for _ in range(m+1)] for _ in range(n+1)]
    
    for i in range(1, n+1):
        dp[i][0] = 0
    for j in range(1, m+1):
        dp[0][j] = 0
        
    for i in range(1, n+1):
        for j in range(1, m+1):
            diag = dp[i-1][j-1] + (1 if x[i-1] == y[j-1] else 0)
            up = dp[i-1][j]
            left = dp[i][j-1]
            max_val = max(diag, up, left)
            dp[i][j] = max_val
            if diag == max_val:
                backtrack[i][j].append('diag')
            if up == max_val:
                backtrack[i][j].append('up')
            if left == max_val:
                backtrack[i][j].append('left')
    return dp, backtrack

def get_all_paths(backtrack, i, j):
    if i == 0 and j == 0:
        return [[(0, 0)]]
    if i == 0:
        return [[(0, k) for k in range(j + 1)]]
    if j == 0:
        return [[(k, 0) for k in range(i + 1)]]
    
    paths = []
    for move in backtrack[i][j]:
        if move == 'diag':
            new_i, new_j = i - 1, j - 1
        elif move == 'up':
            new_i, new_j = i - 1, j
        elif move == 'left':
            new_i, new_j = i, j - 1
        for path in get_all_paths(backtrack, new_i, new_j):
            paths.append(path + [(i, j)])
    return paths

def main():
    x = "TGCATAC"
    y = "ATCTGATC"
    
    dp, backtrack_tbl = lcs_dp_multi(x, y)
    
    main_path = backtrack_path(backtrack_tbl, x, y)
    print("\nCammino ottimo (scelto):", main_path)
    
    all_paths = get_all_paths(backtrack_tbl, len(x), len(y))    
    print(f"\nNumero totale di cammini ottimi: {len(all_paths)}")
    
    for p in all_paths:
        print(p)
    
    lcs = []
    i, j = 0, 0
    for (i2, j2) in main_path[1:]:
        if i2 == i+1 and j2 == j+1 and x[i] == y[j]:
            lcs.append(x[i])
        i, j = i2, j2
    print("\nLCS relativo al path ottimo scelto:", "".join(lcs),"\n")

    plot_alignment(x, y, dp, main_path, all_paths)
    
if __name__ == "__main__":
    main()