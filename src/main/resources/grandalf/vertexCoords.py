from grandalf.graphs import Vertex,Edge,Graph
from grandalf.layouts import DigcoLayout

def createVertex(name, width=10, height=10):
    return Vertex(name)


def createEdge(vertexA, vertexB):
    return Edge(vertexA, vertexB)


def getVertexPositions(vertices, edges):
    print vertices
    print edges

    # Create the vertices
    # Create the edges
    V = vertices
    E = edges

    # Set the size for each vertex
    class defaultview(object):
        w, h = 80, 50

    for v in V: v.view = defaultview()

    g = Graph(V, E)
    dco = DigcoLayout(g.C[0])
    dco.init_all()
    dco.draw(10)

    toReturn = []
    for v in g.C[0].sV:
        toReturn.append([v.data, v.view.xy[0], v.view.xy[1]])

    return toReturn