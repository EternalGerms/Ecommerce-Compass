let currentProductSortField = 'id'; // Inicializa variável para ordenação de produtos
let currentVendaSortField = 'id'; // Inicializa variável para ordenação de vendas

let productSortDirection = 1; // Direção inicial da ordenação de produtos
let vendaSortDirection = 1; // Direção inicial da ordenação de vendas

// Função para ordenar produtos
function sortProducts(field) {
    if (field === currentProductSortField) {
        productSortDirection = -productSortDirection; // Inverte a direção da ordenação se clicar no mesmo campo
    } else {
        currentProductSortField = field;
        productSortDirection = 1; // Começa com ordenação crescente ao clicar em um novo campo
    }
    loadProducts(); // Recarrega os produtos com a nova ordenação
}

// Função para ordenar vendas
function sortVendas(field) {
    if (field === currentVendaSortField) {
        vendaSortDirection = -vendaSortDirection; // Inverte a direção da ordenação se clicar no mesmo campo
    } else {
        currentVendaSortField = field;
        vendaSortDirection = 1; // Começa com ordenação crescente ao clicar em um novo campo
    }
    loadVendas(); // Recarrega as vendas com a nova ordenação
}

// Função para carregar produtos
function loadProducts() {
    fetch('/api/produtos')
    .then(response => {
        if (!response.ok) {
            return response.json().then(error => { throw new Error(error.message || 'Erro ao carregar produtos'); });
        }
        return response.json();
    })
    .then(data => {
        const productList = document.getElementById('productList');
        productList.innerHTML = ''; // Limpa a lista antes de adicionar os novos produtos

        if (data.length === 0) {
            const noDataRow = document.createElement('tr');
            const noDataCell = document.createElement('td');
            noDataCell.colSpan = 4; // Colspan para ocupar todas as colunas da tabela
            noDataCell.innerText = 'Nenhum produto encontrado.';
            noDataRow.appendChild(noDataCell);
            productList.appendChild(noDataRow);
        } else {
            // Ordena os produtos com base no campo e direção atuais
            data.sort((a, b) => {
                const fieldA = a[currentProductSortField];
                const fieldB = b[currentProductSortField];
                
                if (typeof fieldA === 'string') {
                    return productSortDirection * fieldA.localeCompare(fieldB);
                } else {
                    return productSortDirection * (fieldA - fieldB);
                }
            });

            data.forEach(product => {
                const row = document.createElement('tr');
                
                // Coluna ID
                const idCell = document.createElement('td');
                idCell.innerText = product.id;
                row.appendChild(idCell);

                // Coluna Nome
                const nomeCell = document.createElement('td');
                nomeCell.innerText = product.nome;
                row.appendChild(nomeCell);

                // Coluna Preço
                const precoCell = document.createElement('td');
                precoCell.innerText = product.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
                row.appendChild(precoCell);

                // Coluna Estoque
                const estoqueCell = document.createElement('td');
                estoqueCell.innerText = product.estoque;
                row.appendChild(estoqueCell);

                productList.appendChild(row);
            });
        }
    })
    .catch(error => {
        console.log('Erro ao carregar produtos:', error);
        const productList = document.getElementById('productList');
        productList.innerHTML = ''; // Limpa a lista em caso de erro
        const errorRow = document.createElement('tr');
        const errorCell = document.createElement('td');
        errorCell.colSpan = 4;
        errorCell.innerText = 'Erro ao carregar produtos: ' + error.message;
        errorRow.appendChild(errorCell);
        productList.appendChild(errorRow);
    });
}

// Função para carregar vendas
function loadVendas() {
    console.log('Carregando vendas...'); // Log para depuração
    fetch('/api/vendas')
    .then(response => {
        if (!response.ok) {
            return response.json().then(error => { throw new Error(error.message || 'Erro ao carregar vendas'); });
        }
        return response.json();
    })
    .then(data => {
        console.log('Dados de vendas recebidos:', data); // Log para depuração
        const vendaList = document.getElementById('vendaList');
        vendaList.innerHTML = ''; // Limpa a lista antes de adicionar as novas vendas

        if (data.length === 0) {
            const noDataRow = document.createElement('tr');
            const noDataCell = document.createElement('td');
            noDataCell.colSpan = 4; // Colspan para ocupar todas as colunas da tabela
            noDataCell.innerText = 'Nenhuma venda encontrada.';
            noDataRow.appendChild(noDataCell);
            vendaList.appendChild(noDataRow);
        } else {
            // Ordena as vendas com base no campo e direção atuais
            data.sort((a, b) => {
                const fieldA = a[currentVendaSortField];
                const fieldB = b[currentVendaSortField];
                
                if (typeof fieldA === 'string') {
                    return vendaSortDirection * fieldA.localeCompare(fieldB);
                } else {
                    return vendaSortDirection * (fieldA - fieldB);
                }
            });

            data.forEach(venda => {
                const row = document.createElement('tr');
                
                // Coluna ID
                const idCell = document.createElement('td');
                idCell.innerText = venda.id;
                row.appendChild(idCell);

                // Coluna ID do Produto
                const productIdCell = document.createElement('td');
                productIdCell.innerText = venda['id-produto'];
                row.appendChild(productIdCell);

                // Coluna Quantidade
                const quantidadeCell = document.createElement('td');
                quantidadeCell.innerText = venda.quantidade;
                row.appendChild(quantidadeCell);

                // Coluna Data da Venda
                const dataVendaCell = document.createElement('td');
                dataVendaCell.innerText = venda.dataVenda;
                row.appendChild(dataVendaCell);

                vendaList.appendChild(row);
            });
        }
    })
    .catch(error => {
        console.log('Erro ao carregar vendas:', error);
        const vendaList = document.getElementById('vendaList');
        vendaList.innerHTML = ''; // Limpa a lista em caso de erro
        const errorRow = document.createElement('tr');
        const errorCell = document.createElement('td');
        errorCell.colSpan = 4;
        errorCell.innerText = 'Erro ao carregar vendas: ' + error.message;
        errorRow.appendChild(errorCell);
        vendaList.appendChild(errorRow);
    });
}

// Carrega os produtos e vendas inicialmente quando a página carrega
loadProducts();
loadVendas();

document.getElementById('createProductForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const productName = document.getElementById('productName').value;
    const productStock = document.getElementById('productStock').value;
    const productPrice = document.getElementById('productPrice').value;

    fetch('/api/produtos', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            nome: productName,
            estoque: productStock,
            preco: productPrice,
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('createProductResult').innerText = 'Produto criado com sucesso!';
        loadProducts(); // Refresh the product list after creating a new product
    })
    .catch(error => {
        alert('Erro ao criar produto: ' + error.message);
        document.getElementById('createProductResult').innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('editProductForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const productId = document.getElementById('editProductId').value;
    const productName = document.getElementById('editProductName').value;
    const productStock = document.getElementById('editProductStock').value;
    const productPrice = document.getElementById('editProductPrice').value;

    fetch(`/api/produtos/${productId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            nome: productName,
            estoque: productStock,
            preco: productPrice,
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('editProductResult').innerText = 'Produto atualizado com sucesso!';
        loadProducts(); // Refresh the product list after updating a product
    })
    .catch(error => {
        alert('Erro ao editar produto: ' + error.message);
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
    .then(response => {
        if (!response.ok) {
            return response.json().then(error => { throw new Error(error.message || 'Erro ao criar venda'); });
        }
        return response.json();
    })
    .then(data => {
        document.getElementById('createVendaResult').innerText = 'Venda criada com sucesso!';
        loadVendas(); // Refresh the venda list after creating a new venda
        // Clear form fields
        document.getElementById('vendaProductId').value = '';
        document.getElementById('vendaQuantidade').value = '';
        document.getElementById('vendaData').value = '';
    })
    .catch(error => {
        alert('Erro ao criar venda: ' + error.message);
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
            produto_id: productId,
            quantidade: quantidade,
            data_venda: dataVenda,
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('editVendaResult').innerText = 'Venda atualizada com sucesso!';
        loadVendas(); // Refresh the sales list after updating a sale
    })
    .catch(error => {
        alert('Erro ao editar venda: ' + error.message);
        document.getElementById('editVendaResult').innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('filterVendasForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const startDate = document.getElementById('start-date').value;
    const endDate = document.getElementById('end-date').value;

    fetch(`/api/vendas/filtrar?startDate=${startDate}&endDate=${endDate}`)
    .then(response => {
        if (!response.ok) {
            return response.json().then(error => { throw new Error(error.message || 'Erro ao filtrar vendas'); });
        }
        return response.json();
    })
    .then(data => {
        const filterVendasTableBody = document.querySelector('#filterVendasTable tbody');
        filterVendasTableBody.innerHTML = ''; // Clear previous results
        if (data.length === 0) {
            const noDataRow = document.createElement('tr');
            const noDataCell = document.createElement('td');
            noDataCell.colSpan = 4;
            noDataCell.innerText = 'Nenhuma venda encontrada no intervalo especificado.';
            noDataRow.appendChild(noDataCell);
            filterVendasTableBody.appendChild(noDataRow);
        } else {
            data.forEach(venda => {
                const row = document.createElement('tr');
                const idCell = document.createElement('td');
                const productIdCell = document.createElement('td');
                const quantidadeCell = document.createElement('td');
                const dataVendaCell = document.createElement('td');

                idCell.innerText = venda.id;
                productIdCell.innerText = venda['id-produto'];
                quantidadeCell.innerText = venda.quantidade;
                dataVendaCell.innerText = venda.dataVenda;

                row.appendChild(idCell);
                row.appendChild(productIdCell);
                row.appendChild(quantidadeCell);
                row.appendChild(dataVendaCell);

                filterVendasTableBody.appendChild(row);
            });
        }
    })
    .catch(error => {
        alert('Erro ao filtrar vendas: ' + error.message);
        const filterVendasResult = document.getElementById('filterVendasResult');
        filterVendasResult.innerText = 'Erro: ' + error.message;
    });
});

document.getElementById('monthlyReportForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const reportMonth = document.getElementById('reportMonth').value + '-01';

    fetch(`/api/vendas/relatorio-mensal?mesAno=${reportMonth}`)
    .then(response => {
        if (!response.ok) {
            return response.json().then(error => { throw new Error(error.message || 'Erro ao gerar relatório mensal'); });
        }
        return response.json();
    })
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