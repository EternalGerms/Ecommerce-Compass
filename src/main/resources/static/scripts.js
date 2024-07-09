let currentProductSortField = "id"; // Inicializa variável para ordenação de produtos
let currentVendaSortField = "id"; // Inicializa variável para ordenação de vendas

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
  fetch("/api/produtos")
    .then((response) => {
      if (!response.ok) {
        return response.json().then((error) => {
          throw new Error(error.message || "Erro ao carregar produtos");
        });
      }
      return response.json();
    })
    .then((data) => {
      const productList = document.getElementById("productList");
      productList.innerHTML = ""; // Limpa a lista antes de adicionar os novos produtos

      if (data.length === 0) {
        const noDataRow = document.createElement("tr");
        const noDataCell = document.createElement("td");
        noDataCell.colSpan = 6; // Colspan para ocupar todas as colunas da tabela
        noDataCell.innerText = "Nenhum produto encontrado.";
        noDataRow.appendChild(noDataCell);
        productList.appendChild(noDataRow);
      } else {
        // Ordena os produtos com base no campo e direção atuais
        data.sort((a, b) => {
          const fieldA = a[currentProductSortField];
          const fieldB = b[currentProductSortField];

          if (typeof fieldA === "string") {
            return productSortDirection * fieldA.localeCompare(fieldB);
          } else {
            return productSortDirection * (fieldA - fieldB);
          }
        });

        data.forEach((product) => {
          const row = document.createElement("tr");

          // Coluna ID
          const idCell = document.createElement("td");
          idCell.innerText = product.id;
          row.appendChild(idCell);

          // Coluna Nome
          const nomeCell = document.createElement("td");
          nomeCell.innerText = product.nome;
          row.appendChild(nomeCell);

          // Coluna Estoque
          const estoqueCell = document.createElement("td");
          estoqueCell.innerText = product.estoque; // Garante que o estoque seja um número inteiro
          row.appendChild(estoqueCell);

          // Coluna Preço
          const precoCell = document.createElement("td");
          precoCell.innerText = product.preco.toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL",
          }); // Formata o preço como moeda
          row.appendChild(precoCell);

          // Coluna Ativo
          const ativoCell = document.createElement("td");
          ativoCell.innerText = product.ativo ? "Sim" : "Não";
          row.appendChild(ativoCell);

          // Coluna Ações
          const acoesCell = document.createElement("td");
          const editButton = document.createElement("button");
          editButton.innerText = "Editar";
          editButton.addEventListener("click", () => editProduct(product));
          acoesCell.appendChild(editButton);

          const deleteButton = document.createElement("button");
          deleteButton.innerText = "Excluir";
          deleteButton.addEventListener("click", () =>
            deleteProduct(product.id)
          );
          acoesCell.appendChild(deleteButton);

          row.appendChild(acoesCell);

          productList.appendChild(row);
        });
      }
    })
    .catch((error) => {
      console.log("Erro ao carregar produtos:", error);
      const productList = document.getElementById("productList");
      productList.innerHTML = ""; // Limpa a lista em caso de erro
      const errorRow = document.createElement("tr");
      const errorCell = document.createElement("td");
      errorCell.colSpan = 6;
      errorCell.innerText = "Erro ao carregar produtos: " + error.message;
      errorRow.appendChild(errorCell);
      productList.appendChild(errorRow);
    });
}

function editProduct(product) {
  document.getElementById("editProductId").value = product.id;
  document.getElementById("editProductName").value = product.nome;
  document.getElementById("editProductStock").value = product.estoque;
  document.getElementById("editProductPrice").value = product.preco;
}

function deleteProduct(productId) {
  if (confirm("Tem certeza que deseja excluir este produto?")) {
    fetch(`/api/produtos/${productId}`, {
      method: "DELETE",
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((error) => {
            throw new Error(error.message || "Erro ao excluir produto");
          });
        }
        loadProducts(); // Refresh the product list after deleting a product
      })
      .catch((error) => {
        alert("Erro ao excluir produto: " + error.message);
      });
  }
}

// Função para carregar vendas
function loadVendas() {
  console.log("Carregando vendas..."); // Log para depuração
  fetch("/api/vendas")
    .then((response) => {
      if (!response.ok) {
        return response.json().then((error) => {
          throw new Error(error.message || "Erro ao carregar vendas");
        });
      }
      return response.json();
    })
    .then((data) => {
      console.log("Dados de vendas recebidos:", data); // Log para depuração
      const vendaList = document.getElementById("vendaList");
      if (!vendaList) {
        console.error("Elemento vendaList não encontrado no DOM");
        return;
      }
      vendaList.innerHTML = ""; // Limpa a lista antes de adicionar as novas vendas

      if (data.length === 0) {
        const noDataRow = document.createElement("tr");
        const noDataCell = document.createElement("td");
        noDataCell.colSpan = 5; // Colspan para ocupar todas as colunas da tabela
        noDataCell.innerText = "Nenhuma venda encontrada.";
        noDataRow.appendChild(noDataCell);
        vendaList.appendChild(noDataRow);
      } else {
        // Ordena as vendas com base no campo e direção atuais
        data.sort((a, b) => {
          const fieldA = a[currentVendaSortField];
          const fieldB = b[currentVendaSortField];

          if (typeof fieldA === "string") {
            return vendaSortDirection * fieldA.localeCompare(fieldB);
          } else {
            return vendaSortDirection * (fieldA - fieldB);
          }
        });

        data.forEach((venda) => {
          const row = document.createElement("tr");

          // Coluna ID
          const idCell = document.createElement("td");
          idCell.innerText = venda.id;
          row.appendChild(idCell);

          // Coluna ID do Produto
          const productIdCell = document.createElement("td");
          productIdCell.innerText = venda["id-produto"];
          row.appendChild(productIdCell);

          // Coluna Quantidade
          const quantidadeCell = document.createElement("td");
          quantidadeCell.innerText = venda.quantidade;
          row.appendChild(quantidadeCell);

          // Coluna Data da Venda
          const dataVendaCell = document.createElement("td");
          dataVendaCell.innerText = venda.dataVenda;
          row.appendChild(dataVendaCell);

          // Coluna Ações
          const acoesCell = document.createElement("td");
          const editButton = document.createElement("button");
          editButton.innerText = "Editar";
          editButton.addEventListener("click", () => editVenda(venda));
          acoesCell.appendChild(editButton);

          const deleteButton = document.createElement("button");
          deleteButton.innerText = "Excluir";
          deleteButton.addEventListener("click", () => deleteVenda(venda.id));
          acoesCell.appendChild(deleteButton);

          row.appendChild(acoesCell);

          vendaList.appendChild(row);
        });
      }
    })
    .catch((error) => {
      console.log("Erro ao carregar vendas:", error);
      const vendaList = document.getElementById("vendaList");
      if (!vendaList) {
        console.error("Elemento vendaList não encontrado no DOM");
        return;
      }
      vendaList.innerHTML = ""; // Limpa a lista em caso de erro
      const errorRow = document.createElement("tr");
      const errorCell = document.createElement("td");
      errorCell.colSpan = 5;
      errorCell.innerText = "Erro ao carregar vendas: " + error.message;
      errorRow.appendChild(errorCell);
      vendaList.appendChild(errorRow);
    });
}

function editVenda(venda) {
  document.getElementById("editVendaId").value = venda.id;
  document.getElementById("editVendaProductId").value = venda["id-produto"];
  document.getElementById("editVendaQuantidade").value = venda.quantidade;
  document.getElementById("editVendaData").value = venda.dataVenda.substring(
    0,
    16
  ); // Formato ISO 8601 sem os segundos
}

function deleteVenda(vendaId) {
  if (confirm("Tem certeza que deseja excluir esta venda?")) {
    fetch(`/api/vendas/${vendaId}`, {
      method: "DELETE",
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((error) => {
            throw new Error(error.message || "Erro ao excluir venda");
          });
        }
        loadVendas(); // Refresh the venda list after deleting a venda
      })
      .catch((error) => {
        alert("Erro ao excluir venda: " + error.message);
      });
  }
}

// Carrega os produtos e vendas inicialmente quando a página carrega
loadProducts();
loadVendas();

document
  .getElementById("createProductForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    const productName = document.getElementById("productName").value;
    const productStock = document.getElementById("productStock").value;
    const productPrice = document.getElementById("productPrice").value;

    fetch("/api/produtos", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        nome: productName,
        estoque: productStock,
        preco: productPrice,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        document.getElementById("createProductResult").innerText =
          "Produto criado com sucesso!";
        loadProducts(); // Refresh the product list after creating a new product
      })
      .catch((error) => {
        alert("Erro ao criar produto: " + error.message);
        document.getElementById("createProductResult").innerText =
          "Erro: " + error.message;
      });
  });

document
  .getElementById("editProductForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    const productId = document.getElementById("editProductId").value;
    const productName = document.getElementById("editProductName").value;
    const productStock = document.getElementById("editProductStock").value;
    const productPrice = document.getElementById("editProductPrice").value;

    fetch(`/api/produtos/${productId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        nome: productName,
        estoque: productStock,
        preco: productPrice,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        document.getElementById("editProductResult").innerText =
          "Produto atualizado com sucesso!";
        loadProducts(); // Refresh the product list after updating a product
      })
      .catch((error) => {
        alert("Erro ao editar produto: " + error.message);
        document.getElementById("editProductResult").innerText =
          "Erro: " + error.message;
      });
  });

document
  .getElementById("createVendaForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    const productId = document.getElementById("vendaProductId").value;
    const quantidade = document.getElementById("vendaQuantidade").value;
    const dataVenda = document.getElementById("vendaData").value;

    // Formatar a data para o formato ISO 8601
    const formattedDataVenda = dataVenda + ":00Z";

    fetch("/api/vendas", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        idProduto: productId,
        quantidade: quantidade,
        dataVenda: formattedDataVenda,
      }),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((error) => {
            throw new Error(error.message || "Erro ao criar venda");
          });
        }
        return response.json();
      })
      .then((data) => {
        document.getElementById("createVendaResult").innerText =
          "Venda criada com sucesso!";
        loadVendas(); // Refresh the venda list after creating a new venda
        // Clear form fields
        document.getElementById("vendaProductId").value = "";
        document.getElementById("vendaQuantidade").value = "";
        document.getElementById("vendaData").value = "";
      })
      .catch((error) => {
        alert("Erro ao criar venda: " + error.message);
        document.getElementById("createVendaResult").innerText =
          "Erro: " + error.message;
      });
  });

document
  .getElementById("editVendaForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    const vendaId = document.getElementById("editVendaId").value;
    const productId = document.getElementById("editVendaProductId").value;
    const quantidade = document.getElementById("editVendaQuantidade").value;
    const dataVenda = document.getElementById("editVendaData").value;

    // Formatar a data para o formato ISO 8601
    const formattedDataVenda = dataVenda + ":00Z";

    fetch(`/api/vendas/${vendaId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        idProduto: productId,
        quantidade: quantidade,
        dataVenda: formattedDataVenda,
      }),
    })
      .then((response) => {
        console.log("Data sent:", {
          idProduto: productId,
          quantidade: quantidade,
          dataVenda: formattedDataVenda,
        });
        console.log("Response status:", response.status);
        if (!response.ok) {
          return response.json().then((error) => {
            console.error("Server error:", error);
            throw new Error(error.message || "Erro ao editar venda");
          });
        }
        return response.json();
      })
      .then((data) => {
        console.log("Data received:", data);
        document.getElementById("editVendaResult").innerText =
          "Venda atualizada com sucesso!";
        loadVendas(); // Refresh the sales list after updating a sale
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("Erro ao editar venda: " + error.message);
        document.getElementById("editVendaResult").innerText =
          "Erro: " + error.message;
      });
  });

document
  .getElementById("filterVendasForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    const startDate = document.getElementById("start-date").value;
    const endDate = document.getElementById("end-date").value;

    // Formatar as datas para o formato ISO 8601
    const formattedStartDate = startDate + ":00Z";
    const formattedEndDate = endDate + ":00Z";

    fetch(
      `/api/vendas/filtrar?startDate=${formattedStartDate}&endDate=${formattedEndDate}`
    )
      .then((response) => {
        if (!response.ok) {
          return response.json().then((error) => {
            throw new Error(error.message || "Erro ao filtrar vendas");
          });
        }
        return response.json();
      })
      .then((data) => {
        const filterVendasTableBody = document.querySelector(
          "#filterVendasTable tbody"
        );
        filterVendasTableBody.innerHTML = ""; // Clear previous results
        if (data.length === 0) {
          const noDataRow = document.createElement("tr");
          const noDataCell = document.createElement("td");
          noDataCell.colSpan = 4;
          noDataCell.innerText =
            "Nenhuma venda encontrada no intervalo especificado.";
          noDataRow.appendChild(noDataCell);
          filterVendasTableBody.appendChild(noDataRow);
        } else {
          data.forEach((venda) => {
            const row = document.createElement("tr");
            const idCell = document.createElement("td");
            const productIdCell = document.createElement("td");
            const quantidadeCell = document.createElement("td");
            const dataVendaCell = document.createElement("td");

            idCell.innerText = venda.id;
            productIdCell.innerText = venda["id-produto"];
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
      .catch((error) => {
        alert("Erro ao filtrar vendas: " + error.message);
        const filterVendasResult =
          document.getElementById("filterVendasResult");
        filterVendasResult.innerText = "Erro: " + error.message;
      });
  });

// Variáveis para ordenação dos relatórios
let currentMonthlyReportSortField = "data";
let currentWeeklyReportSortField = "data";
let monthlyReportSortDirection = 1;
let weeklyReportSortDirection = 1;

// Função para ordenar relatório mensal
function sortMonthlyReport(field) {
  if (field === currentMonthlyReportSortField) {
    monthlyReportSortDirection = -monthlyReportSortDirection;
  } else {
    currentMonthlyReportSortField = field;
    monthlyReportSortDirection = 1;
  }
  loadMonthlyReport();
}

// Função para ordenar relatório semanal
function sortWeeklyReport(field) {
  if (field === currentWeeklyReportSortField) {
    weeklyReportSortDirection = -weeklyReportSortDirection;
  } else {
    currentWeeklyReportSortField = field;
    weeklyReportSortDirection = 1;
  }
  loadWeeklyReport();
}

// Função para carregar relatório mensal
function loadMonthlyReport() {
    const reportMonth = document.getElementById("reportMonth").value;
    const formattedReportMonth = reportMonth + "-01T00:00:00Z"; // Formata a data para o formato ISO 8601
  
    fetch(`/api/vendas/relatorio/mensal?mesAno=${formattedReportMonth}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok ' + response.statusText);
        }
        return response.json();
      })
      .then(data => {
        // Processa os dados recebidos e exibe no front-end
        const monthlyReportList = document.getElementById("monthlyReportList");
        monthlyReportList.innerHTML = ""; // Limpa a lista antes de adicionar os novos dados
  
        if (data.length === 0) {
          const noDataRow = document.createElement("tr");
          const noDataCell = document.createElement("td");
          noDataCell.colSpan = 4;
          noDataCell.innerText = "Nenhum dado encontrado.";
          noDataRow.appendChild(noDataCell);
          monthlyReportList.appendChild(noDataRow);
        } else {
          data.forEach(item => {
            const row = document.createElement("tr");
            const idCell = document.createElement("td");
            idCell.innerText = item.id;
            row.appendChild(idCell);
  
            const idProdutoCell = document.createElement("td");
            idProdutoCell.innerText = item["id-produto"];
            row.appendChild(idProdutoCell);
  
            const dataCell = document.createElement("td");
            dataCell.innerText = item.dataVenda;
            row.appendChild(dataCell);
  
            const quantidadeCell = document.createElement("td");
            quantidadeCell.innerText = item.quantidade;
            row.appendChild(quantidadeCell);
  
            monthlyReportList.appendChild(row);
          });
        }
      })
      .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
      });
  }

// Função para carregar relatório semanal
function loadWeeklyReport() {
    const reportWeek = document.getElementById("reportWeek").value;
    console.log("reportWeek:", reportWeek); // Adicione este log
    const formattedReportWeek = reportWeek + ":00Z"; // Append seconds and 'Z' for UTC time
    console.log("formattedReportWeek:", formattedReportWeek); // Adicione este log
  
    fetch(`/api/vendas/relatorio/semanal?semana=${encodeURIComponent(formattedReportWeek)}`)
      .then((response) => {
        console.log("Response status:", response.status); // Adicione este log
        if (!response.ok) {
          return response.json().then((error) => {
            throw new Error(error.message || "Erro ao gerar relatório semanal");
          });
        }
        return response.json();
      })
      .then((data) => {
        console.log("Data received:", data); // Adicione este log
        const weeklyReportList = document.getElementById("weeklyReportList");
        if (!weeklyReportList) {
          console.error("Elemento weeklyReportList não encontrado no DOM");
          return;
        }
        weeklyReportList.innerHTML = ""; // Limpa a lista antes de adicionar os novos dados
  
        if (data.length === 0) {
          const noDataRow = document.createElement("tr");
          const noDataCell = document.createElement("td");
          noDataCell.colSpan = 4;
          noDataCell.innerText = "Nenhum dado encontrado.";
          noDataRow.appendChild(noDataCell);
          weeklyReportList.appendChild(noDataRow);
        } else {
          // Ordena os dados com base no campo e direção atuais
          data.sort((a, b) => {
            const fieldA = a[currentWeeklyReportSortField];
            const fieldB = b[currentWeeklyReportSortField];
  
            if (typeof fieldA === "string") {
              return weeklyReportSortDirection * fieldA.localeCompare(fieldB);
            } else {
              return weeklyReportSortDirection * (fieldA - fieldB);
            }
          });
  
          data.forEach((item) => {
            const row = document.createElement("tr");
  
            const idCell = document.createElement("td");
            idCell.innerText = item.id !== undefined ? item.id : "N/A";
            row.appendChild(idCell);
  
            const idProdutoCell = document.createElement("td");
            idProdutoCell.innerText =
              item["id-produto"] !== undefined ? item["id-produto"] : "N/A";
            row.appendChild(idProdutoCell);
  
            const dataCell = document.createElement("td");
            dataCell.innerText =
              item.dataVenda !== undefined ? item.dataVenda : "N/A";
            row.appendChild(dataCell);
  
            const quantidadeCell = document.createElement("td");
            quantidadeCell.innerText =
              item.quantidade !== undefined ? item.quantidade : "N/A";
            row.appendChild(quantidadeCell);
  
            weeklyReportList.appendChild(row);
          });
        }
      })
      .catch((error) => {
        console.error("Error:", error); // Adicione este log
        const weeklyReportResult = document.getElementById("weeklyReportResult");
        if (!weeklyReportResult) {
          console.error("Elemento weeklyReportResult não encontrado no DOM");
          return;
        }
        weeklyReportResult.innerText = "Erro: " + error.message;
      });
  }

document
  .getElementById("monthlyReportForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    loadMonthlyReport();
  });

document
  .getElementById("weeklyReportForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    loadWeeklyReport();
  });
