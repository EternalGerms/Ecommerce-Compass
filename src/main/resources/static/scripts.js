let currentVendaSortField = "id"; // Campo inicial para ordenação das vendas
let vendaSortDirection = "asc"; // Direção inicial da ordenação das vendas

let currentSortField = "id"; // Campo inicial para ordenação
let currentSortDirection = "asc"; // Direção inicial da ordenação

function getAuthToken() {
	return localStorage.getItem('authToken');
}

// Função para adicionar o token ao cabeçalho das requisições
function fetchWithAuth(url, options = {}) {
    const token = getAuthToken();
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }
    return fetch(url, options).then(response => {
        if (!response) {
            throw new Error('Network response was not ok');
        }
        if (response.status === 401) {
            console.error('Unauthorized');
            // Aqui você pode adicionar lógica para redirecionar para a página de login ou mostrar uma mensagem ao usuário
            return null;
        }
        return response.json();
    });
}


function handleFetchError(response) {
	if (!response.ok) {
		return response.json().then(error => {
			throw new Error(error.message || "Something went wrong");
		});
	}
	return response.json();
}

document.getElementById('login-btn').addEventListener('click', () => {
    Swal.fire({
        title: 'Login',
        html: `
            <input id="login-username" class="swal2-input" placeholder="Username">
            <input id="login-password" class="swal2-input" type="password" placeholder="Password">
        `,
        confirmButtonText: 'Login',
        preConfirm: () => {
            const username = document.getElementById('login-username').value;
            const password = document.getElementById('login-password').value;
            return { username: username, password: password };
        }
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(result.value)
            }).then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            }).then(data => {
                const token = data.token;
                localStorage.setItem('authToken', token);
                document.getElementById('username').innerText = result.value.username;
                document.getElementById('user-info').style.display = 'block';
                Swal.fire('Logged in successfully!');
                loadProducts();
                loadVendas();
            }).catch(error => {
                console.error('Error during login:', error);
                Swal.fire('Error', 'Login failed: ' + error.message, 'error');
            });
        }
    });
});
	Swal.fire({
		title: 'Login',
		html: `
            <input id="login-username" class="swal2-input" placeholder="Username">
            <input id="login-password" class="swal2-input" type="password" placeholder="Password">
        `,
		confirmButtonText: 'Login',
		preConfirm: () => {
			const username = document.getElementById('login-username').value;
			const password = document.getElementById('login-password').value;
			return { username: username, password: password };
		}
	}).then((result) => {
		if (result.isConfirmed) {
			fetch('http://localhost:8080/api/auth/login', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(result.value)
			}).then(response => {
				if (!response.ok) {
					throw new Error(`HTTP error! status: ${response.status}`);
				}
				return response.json();
			}).then(data => {
				const token = data.token;
				localStorage.setItem('authToken', token);
				document.getElementById('username').innerText = result.value.username;
				document.getElementById('user-info').style.display = 'block';
				Swal.fire('Logged in successfully!');
				loadProducts();
				loadVendas();
			}).catch(error => {
				console.error('Error during login:', error);
				Swal.fire('Error', 'Login failed: ' + error.message, 'error');
			});
		}
	});

document.getElementById('register-btn').addEventListener('click', () => {
	Swal.fire({
		title: 'Register',
		html: `
            <input id="register-username" class="swal2-input" placeholder="Username">
            <input id="register-password" class="swal2-input" type="password" placeholder="Password">
            <input id="register-email" class="swal2-input" placeholder="Email">
            <select id="register-role" class="swal2-input">
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
            </select>
        `,
		confirmButtonText: 'Register',
		preConfirm: () => {
			const username = document.getElementById('register-username').value;
			const password = document.getElementById('register-password').value;
			const email = document.getElementById('register-email').value;
			const role = document.getElementById('register-role').value;
			return { username: username, password: password, email: email, role: role };
		}
	}).then((result) => {
		if (result.isConfirmed) {
			fetch('/api/auth/register', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(result.value)
			}).then(handleFetchError)
				.then(data => {
					if (data.message === 'User registered successfully.') {
						Swal.fire('Success', data.message, 'success');
					} else {
						Swal.fire('Error', data.message, 'error');
					}
				}).catch(error => {
					Swal.fire('Error', error.message, 'error');
				});
		}
	});
});

// Função para ordenar produtos
function sortProducts(field) {
	if (currentSortField === field) {
		// Inverte a direção se o mesmo campo for clicado novamente
		currentSortDirection = currentSortDirection === "asc" ? "desc" : "asc";
	} else {
		// Define o novo campo e direção inicial
		currentSortField = field;
		currentSortDirection = "asc";
	}
	loadProducts(); // Recarrega os produtos com a nova ordenação
}

// Função para ordenar vendas
function sortVendas(field) {
	if (currentVendaSortField === field) {
		// Inverte a direção se o mesmo campo for clicado novamente
		vendaSortDirection = vendaSortDirection === "asc" ? "desc" : "asc";
	} else {
		// Define o novo campo e direção inicial
		currentVendaSortField = field;
		vendaSortDirection = "asc";
	}
	loadVendas(); // Recarrega as vendas com a nova ordenação
}

// Função para carregar produtos
function loadProducts() {
	fetchWithAuth("/api/produtos")
		.then((response) => {
			if (!response.ok) {
				return response.json().then((error) => {
					if (error.code === 404) {
						return { code: 404, message: error.message };
					}
					throw new Error(error.message || "Erro ao carregar produtos");
				});
			}
			return response.json();
		})
		.then((data) => {
			const productList = document.getElementById("productList");
			productList.innerHTML = ""; // Limpa a lista antes de adicionar os novos produtos

			if (data.code === 404) {
				const noDataRow = document.createElement("tr");
				const noDataCell = document.createElement("td");
				noDataCell.colSpan = 5; // Colspan para ocupar todas as colunas da tabela
				noDataCell.innerText = data.message;
				noDataRow.appendChild(noDataCell);
				productList.appendChild(noDataRow);
				return;
			}

			if (!Array.isArray(data)) {
				console.error(
					"Dados de produtos não estão no formato esperado. Tipo recebido:",
					typeof data
				);
				console.error("Dados recebidos:", data);
				const errorRow = document.createElement("tr");
				const errorCell = document.createElement("td");
				errorCell.colSpan = 5;
				errorCell.innerText =
					"Erro ao carregar produtos: formato de dados inesperado.";
				errorRow.appendChild(errorCell);
				productList.appendChild(errorRow);
				return;
			}

			if (data.length === 0) {
				const noDataRow = document.createElement("tr");
				const noDataCell = document.createElement("td");
				noDataCell.colSpan = 5; // Colspan para ocupar todas as colunas da tabela
				noDataCell.innerText = "Nenhum produto encontrado.";
				noDataRow.appendChild(noDataCell);
				productList.appendChild(noDataRow);
			} else {
				// Ordena os produtos com base no campo e direção atuais
				data.sort((a, b) => {
					const fieldA = a[currentSortField];
					const fieldB = b[currentSortField];

					if (fieldA < fieldB) {
						return currentSortDirection === "asc" ? -1 : 1;
					} else if (fieldA > fieldB) {
						return currentSortDirection === "asc" ? 1 : -1;
					} else {
						return 0;
					}
				});

				data.forEach((product) => {
					const row = document.createElement("tr");

					// Coluna ID
					const idCell = document.createElement("td");
					idCell.innerText = product.id;
					row.appendChild(idCell);

					// Coluna Nome
					const nameCell = document.createElement("td");
					nameCell.innerText = product.nome;
					row.appendChild(nameCell);

					// Coluna Estoque
					const stockCell = document.createElement("td");
					stockCell.innerText = product.estoque;
					row.appendChild(stockCell);

					// Coluna Preço
					const priceCell = document.createElement("td");
					priceCell.innerText = product.preco;
					row.appendChild(priceCell);

					// Coluna Ativo
					const activeCell = document.createElement("td");
					activeCell.innerText = product.ativo ? "Sim" : "Não";
					row.appendChild(activeCell);

					// Coluna Ações
					const actionsCell = document.createElement("td");
					const editButton = document.createElement("button");
					editButton.innerText = "Editar";
					editButton.addEventListener("click", () => editProduct(product));
					actionsCell.appendChild(editButton);

					const deleteButton = document.createElement("button");
					deleteButton.innerText = "Excluir";
					deleteButton.addEventListener("click", () => deleteProduct(product.id));
					actionsCell.appendChild(deleteButton);

					row.appendChild(actionsCell);

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
			errorCell.colSpan = 5;
			errorCell.innerText = "Erro ao carregar produtos: " + error.message;
			errorRow.appendChild(errorCell);
			productList.appendChild(errorRow);
		});
}
// Função para carregar vendas
function loadVendas() {
	fetchWithAuth("/api/vendas")
		.then((response) => {
			if (!response.ok) {
				return response.json().then((error) => {
					if (error.code === 404) {
						return { code: 404, message: error.message };
					}
					throw new Error(error.message || "Erro ao carregar vendas");
				});
			}
			return response.json();
		})
		.then((data) => {
			const vendaList = document.getElementById("vendaList");
			vendaList.innerHTML = ""; // Limpa a lista antes de adicionar as novas vendas

			if (data.code === 404) {
				const noDataRow = document.createElement("tr");
				const noDataCell = document.createElement("td");
				noDataCell.colSpan = 5; // Colspan para ocupar todas as colunas da tabela
				noDataCell.innerText = data.message;
				noDataRow.appendChild(noDataCell);
				vendaList.appendChild(noDataRow);
				return;
			}

			if (!Array.isArray(data)) {
				console.error(
					"Dados de vendas não estão no formato esperado. Tipo recebido:",
					typeof data
				);
				console.error("Dados recebidos:", data);
				const errorRow = document.createElement("tr");
				const errorCell = document.createElement("td");
				errorCell.colSpan = 5;
				errorCell.innerText =
					"Erro ao carregar vendas: formato de dados inesperado.";
				errorRow.appendChild(errorCell);
				vendaList.appendChild(errorRow);
				return;
			}

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

					if (fieldA < fieldB) {
						return vendaSortDirection === "asc" ? -1 : 1;
					} else if (fieldA > fieldB) {
						return vendaSortDirection === "asc" ? 1 : -1;
					} else {
						return 0;
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
			vendaList.innerHTML = ""; // Limpa a lista em caso de erro
			const errorRow = document.createElement("tr");
			const errorCell = document.createElement("td");
			errorCell.colSpan = 5;
			errorCell.innerText = "Erro ao carregar vendas: " + error.message;
			errorRow.appendChild(errorCell);
			vendaList.appendChild(errorRow);
		});
}

function editProduct(product) {
	document.getElementById("editProductId").value = product.id;
	document.getElementById("editProductName").value = product.nome;
	document.getElementById("editProductStock").value = product.estoque;
	document.getElementById("editProductPrice").value = product.preco;
	document.getElementById("editProductActive").checked = product.ativo;
}

function deleteProduct(productId) {
	if (confirm("Tem certeza que deseja excluir este produto?")) {
		fetchWithAuth(`/api/produtos/${productId}`, {
			method: "DELETE",
		})
			.then((response) => {
				if (!response.ok) {
					return response.json().then((error) => {
						throw new Error(error.message || "Erro ao excluir produto");
					});
				}
				// Verifica se a resposta está vazia
				return response.text().then(text => text ? JSON.parse(text) : {});
			})
			.then((data) => {
				Swal.fire({
					icon: "success",
					title: "Produto Excluído",
					text: "Produto excluído com sucesso.",
				});
				loadProducts(); // Recarregar produtos após a exclusão
			})
			.catch((error) => {
				Swal.fire({
					icon: "warning",
					title: "Produto Inativado",
					text: error.message,
				});
				loadProducts(); // Recarregar produtos após a inativação
			});
	}
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
		fetchWithAuth(`/api/vendas/${vendaId}`, {
			method: "DELETE",
		})
			.then((response) => {
				if (!response.ok) {
					return response.json().then((error) => {
						throw new Error(error.message || "Erro ao excluir venda");
					});
				}
				loadVendas(); // Recarregar vendas após a exclusão
			})
			.catch((error) => {
				alert("Erro ao excluir venda: " + error.message);
			});
	}
}

// Carrega os produtos e vendas inicialmente quando a página carrega
loadProducts();
loadVendas();

document.getElementById('createProductForm').addEventListener('submit', function(e) {
	e.preventDefault();
	const productName = document.getElementById("productName").value;
	const productStock = document.getElementById("productStock").value;
	const productPrice = document.getElementById("productPrice").value;

	fetchWithAuth("/api/produtos", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			nome: productName,
			estoque: productStock,
			preco: productPrice,
		}),
	}).then(handleFetchError)
		.then((data) => {
			document.getElementById("createProductResult").innerText = "Produto criado com sucesso!";
			loadProducts();
		}).catch((error) => {
			Swal.fire('Error', error.message, 'error');
		});
});

document.getElementById('editProductForm').addEventListener('submit', function(e) {
	e.preventDefault();
	const productId = document.getElementById("editProductId").value;
	const productName = document.getElementById("editProductName").value;
	const productStock = document.getElementById("editProductStock").value;
	const productPrice = document.getElementById("editProductPrice").value;
	const productActive = document.getElementById("editProductActive").checked;

	fetchWithAuth(`/api/produtos/${productId}`, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			nome: productName,
			estoque: productStock,
			preco: productPrice,
			ativo: productActive,
		}),
	}).then(handleFetchError)
		.then((data) => {
			document.getElementById("editProductResult").innerText = "Produto atualizado com sucesso!";
			loadProducts();
		}).catch((error) => {
			Swal.fire('Error', error.message, 'error');
		});
});


document.getElementById('createVendaForm').addEventListener('submit', function(e) {
	e.preventDefault();
	const productId = document.getElementById("vendaProductId").value;
	const quantidade = document.getElementById("vendaQuantidade").value;
	const dataVenda = document.getElementById("vendaData").value;

	const formattedDataVenda = dataVenda + ":00Z";

	fetchWithAuth("/api/vendas", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			idProduto: productId,
			quantidade: quantidade,
			dataVenda: formattedDataVenda,
		}),
	}).then(handleFetchError)
		.then((data) => {
			document.getElementById("createVendaResult").innerText = "Venda criada com sucesso!";
			loadVendas();
			document.getElementById("vendaProductId").value = "";
			document.getElementById("vendaQuantidade").value = "";
			document.getElementById("vendaData").value = "";
		}).catch((error) => {
			Swal.fire('Error', error.message, 'error');
		});
});

document.getElementById('editVendaForm').addEventListener('submit', function(e) {
	e.preventDefault();
	const vendaId = document.getElementById("editVendaId").value;
	const productId = document.getElementById("editVendaProductId").value;
	const quantidade = document.getElementById("editVendaQuantidade").value;
	const dataVenda = document.getElementById("editVendaData").value;

	const formattedDataVenda = dataVenda + ":00Z";

	fetchWithAuth(`/api/vendas/${vendaId}`, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			idProduto: productId,
			quantidade: quantidade,
			dataVenda: formattedDataVenda,
		}),
	}).then(handleFetchError)
		.then((data) => {
			document.getElementById("editVendaResult").innerText = "Venda atualizada com sucesso!";
			loadVendas();
		}).catch((error) => {
			Swal.fire('Error', error.message, 'error');
		});
});


document.getElementById('editVendaForm').addEventListener('submit', function(e) {
	e.preventDefault();
	const vendaId = document.getElementById("editVendaId").value;
	const productId = document.getElementById("editVendaProductId").value;
	const quantidade = document.getElementById("editVendaQuantidade").value;
	const dataVenda = document.getElementById("editVendaData").value;

	// Formatar a data para o formato ISO 8601
	const formattedDataVenda = dataVenda + ":00Z";

	fetchWithAuth(`/api/vendas/${vendaId}`, {
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
			if (!response.ok) {
				return response.json().then((error) => {
					throw new Error(`${error.code}: ${error.message}`);
				});
			}
			return response.json();
		})
		.then((data) => {
			document.getElementById("editVendaResult").innerText = "Venda atualizada com sucesso!";
			loadVendas(); // Recarregar vendas após a edição
		})
		.catch((error) => {
			const errorMessage = `Erro ${error.message}`;
			alert(errorMessage);
			document.getElementById("editVendaResult").innerText = errorMessage;
		});
});
document
	.getElementById("filterVendasForm")
	.addEventListener("submit", function(e) {
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
						throw new Error(`${error.code}: ${error.message}`);
					});
				}
				return response.json();
			})
			.then((data) => {
				const filterVendasTableBody = document.querySelector(
					"#filterVendasTable tbody"
				);

				// Verifica se o elemento existe
				if (!filterVendasTableBody) {
					console.error(
						"Elemento #filterVendasTable tbody não encontrado no DOM"
					);
					return;
				}

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
				const errorMessage = `Erro ${error.message}`;
				alert(errorMessage);
				const filterVendasResult =
					document.getElementById("filterVendasResult");
				filterVendasResult.innerText = errorMessage;
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
		.then((response) => {
			if (!response.ok) {
				throw new Error("Network response was not ok " + response.statusText);
			}
			return response.json();
		})
		.then((data) => {
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
				data.forEach((item) => {
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
		.catch((error) => {
			console.error("There was a problem with the fetch operation:", error);
		});
}

// Função para carregar relatório semanal
function loadWeeklyReport() {
	const reportWeek = document.getElementById("reportWeek").value;
	console.log("reportWeek:", reportWeek); // Adicione este log
	const formattedReportWeek = reportWeek + ":00Z"; // Append seconds and 'Z' for UTC time
	console.log("formattedReportWeek:", formattedReportWeek); // Adicione este log

	fetch(
		`/api/vendas/relatorio/semanal?semana=${encodeURIComponent(
			formattedReportWeek
		)}`
	)
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
	.addEventListener("submit", function(e) {
		e.preventDefault();
		loadMonthlyReport();
	});

document
	.getElementById("weeklyReportForm")
	.addEventListener("submit", function(e) {
		e.preventDefault();
		loadWeeklyReport();
	});


document.getElementById('logout-btn').addEventListener('click', () => {
	localStorage.removeItem('authToken');
	document.getElementById('user-info').style.display = 'none';
	Swal.fire('Logged out successfully!').then(() => {
		// Redirecionar para a página de login ou para uma página de logout bem-sucedido
		window.location.href = '/';
	});
});

document.addEventListener('DOMContentLoaded', () => {
	const token = localStorage.getItem('authToken');
	if (token) {
		document.getElementById('user-info').style.display = 'block';
	} else {
		document.getElementById('user-info').style.display = 'none';
	}
});