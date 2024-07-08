document.getElementById('createProductForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const productName = document.getElementById('productName').value;
    const productStock = document.getElementById('productStock').value;
    const productPrice = document.getElementById('productPrice').value;
    const productActive = document.getElementById('productActive').checked;

    fetch('/api/produtos', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            nome: productName,
            estoque: productStock,
            preco: productPrice,
            ativo: productActive
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('createProductResult').innerText = JSON.stringify(data, null, 2);
        loadProducts(); // Refresh the product list after creating a new product
    })
    .catch(error => {
        document.getElementById('createProductResult').innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('editProductForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const productId = document.getElementById('editProductId').value;
    const productName = document.getElementById('editProductName').value;
    const productStock = document.getElementById('editProductStock').value;
    const productPrice = document.getElementById('editProductPrice').value;
    const productActive = document.getElementById('editProductActive').checked;

    fetch(`/api/produtos/${productId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            nome: productName,
            estoque: productStock,
            preco: productPrice,
            ativo: productActive
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('editProductResult').innerText = JSON.stringify(data, null, 2);
        loadProducts(); // Refresh the product list after updating a product
    })
    .catch(error => {
        document.getElementById('editProductResult').innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('createVendaForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const productId = document.getElementById('vendaProductId').value;
    const quantidade = document.getElementById('vendaQuantidade').value;
    const dataVenda = document.getElementById('vendaData').value;

    fetch('/api/vendas', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            idProduto: productId,
            quantidade: quantidade,
            dataVenda: dataVenda
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('createVendaResult').innerText = JSON.stringify(data, null, 2);
        loadVendas(); // Refresh the venda list after creating a new venda
    })
    .catch(error => {
        document.getElementById('createVendaResult').innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('editVendaForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const vendaId = document.getElementById('editVendaId').value;
    const productId = document.getElementById('editVendaProductId').value;
    const quantidade = document.getElementById('editVendaQuantidade').value;
    const dataVenda = document.getElementById('editVendaData').value;

    fetch(`/api/vendas/${vendaId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            idProduto: productId,
            quantidade: quantidade,
            dataVenda: dataVenda
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('editVendaResult').innerText = JSON.stringify(data, null, 2);
        loadVendas(); // Refresh the venda list after updating a venda
    })
    .catch(error => {
        document.getElementById('editVendaResult').innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('monthlyReportForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const reportMonth = document.getElementById('reportMonth').value + '-01';

    fetch(`/api/vendas/relatorio-mensal?mesAno=${reportMonth}`)
    .then(response => response.json())
    .then(data => {
        document.getElementById('monthlyReportResult').innerText = JSON.stringify(data, null, 2);
    })
    .catch(error => {
        document.getElementById('monthlyReportResult').innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('weeklyReportForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const reportWeek = document.getElementById('reportWeek').value;

    fetch(`/api/vendas/relatorio-semanal?semana=${reportWeek}`)
    .then(response => response.json())
    .then(data => {
        document.getElementById('weeklyReportResult').innerText = JSON.stringify(data, null, 2);
    })
    .catch(error => {
        document.getElementById('weeklyReportResult').innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('refreshProductList').addEventListener('click', loadProducts);
document.getElementById('refreshVendaList').addEventListener('click', loadVendas);

function loadProducts() {
    fetch('/api/produtos')
    .then(response => response.json())
    .then(data => {
        const productList = document.getElementById('productList');
        productList.innerHTML = ''; // Clear the list before adding new products
        if (data.length === 0) {
            productList.innerText = 'Nenhum produto encontrado.';
        } else {
            const ul = document.createElement('ul');
            data.forEach(product => {
                const li = document.createElement('li');
                li.innerText = `ID: ${product.id}, Nome: ${product.nome}, Estoque: ${product.estoque}, Preço: ${product.preco}, Ativo: ${product.ativo}`;
                
                const editButton = document.createElement('button');
                editButton.innerText = 'Editar';
                editButton.addEventListener('click', () => {
                    document.getElementById('editProductId').value = product.id;
                    document.getElementById('editProductName').value = product.nome;
                    document.getElementById('editProductStock').value = product.estoque;
                    document.getElementById('editProductPrice').value = product.preco;
                    document.getElementById('editProductActive').checked = product.ativo;
                });
                
                const deleteButton = document.createElement('button');
                deleteButton.innerText = 'Excluir';
                deleteButton.addEventListener('click', () => {
                    fetch(`/api/produtos/${product.id}`, {
                        method: 'DELETE'
                    })
                    .then(() => {
                        loadProducts(); // Refresh the product list after deleting a product
                    })
                    .catch(error => {
                        alert('Erro ao excluir produto: ' + error.message);
                    });
                });

                li.appendChild(editButton);
                li.appendChild(deleteButton);
                ul.appendChild(li);
            });
            productList.appendChild(ul);
        }
    })
    .catch(error => {
        document.getElementById('productList').innerText = 'Erro: ' + error.message;
    });
}

function loadVendas() {
    fetch('/api/vendas')
    .then(response => response.json())
    .then(data => {
        const vendaList = document.getElementById('vendaList');
        vendaList.innerHTML = ''; // Clear the list before adding new vendas
        if (data.length === 0) {
            vendaList.innerText = 'Nenhuma venda encontrada.';
        } else {
            const ul = document.createElement('ul');
            data.forEach(venda => {
                const li = document.createElement('li');
                li.innerText = `ID: ${venda.id}, Produto ID: ${venda.idProduto}, Quantidade: ${venda.quantidade}, Data: ${venda.dataVenda}`;
                
                const editButton = document.createElement('button');
                editButton.innerText = 'Editar';
                editButton.addEventListener('click', () => {
                    document.getElementById('editVendaId').value = venda.id;
                    document.getElementById('editVendaProductId').value = venda.idProduto;
                    document.getElementById('editVendaQuantidade').value = venda.quantidade;
                    document.getElementById('editVendaData').value = venda.dataVenda;
                });
                
                const deleteButton = document.createElement('button');
                deleteButton.innerText = 'Excluir';
                deleteButton.addEventListener('click', () => {
                    fetch(`/api/vendas/${venda.id}`, {
                        method: 'DELETE'
                    })
                    .then(() => {
                        loadVendas(); // Refresh the venda list after deleting a venda
                    })
                    .catch(error => {
                        alert('Erro ao excluir venda: ' + error.message);
                    });
                });

                li.appendChild(editButton);
                li.appendChild(deleteButton);
                ul.appendChild(li);
            });
            vendaList.appendChild(ul);
        }
    })
    .catch(error => {
        document.getElementById('vendaList').innerText = 'Erro: ' + error.message;
    });
}

// Load products and vendas initially when the page loads
loadProducts();
loadVendas();
