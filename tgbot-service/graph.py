import networkx as nx
import matplotlib.pyplot as plt


async def create_graph(path):
    G = nx.DiGraph()

    # добавляем вершины в граф
    for node in path:
        G.add_node(node)

    # добавляем ребра между соседними вершинами
    for i in range(len(path) - 1):
        G.add_edge(path[i], path[i + 1])

        # настраиваем отображение графа
        pos = nx.spring_layout(G)
        plt.figure()
        node_labels = {node: node[:10] + '...' if len(node) > 13 else node for node in G.nodes()}
        nx.draw_networkx_nodes(G, pos)
        nx.draw_networkx_labels(G, pos, labels=node_labels, font_size=10)
        nx.draw_networkx_edges(G, pos)

        # сохраняем изображение и отправляем его пользователю
        plt.savefig("path.png")

