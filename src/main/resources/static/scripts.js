// Constants for sorting
const SORT_ASC = "asc";
const SORT_DESC = "desc";

// Initial sorting fields and directions
let currentVendaSortField = "id";
let vendaSortDirection = SORT_ASC;
let currentSortField = "id";
let currentSortDirection = SORT_ASC;

// Utility functions
function getAuthToken() {
	return localStorage.getItem('authToken');
}

function fetchWithAuth(url, options = {}) {
	const token = getAuthToken();
	if (token) {
		options.headers = {
			...options.headers,
			'Authorization': `Bearer ${token}`
		};
	}
	return fetch(url, options).then(response => {
		if (!response.ok) {
			return response.json().then(error => {
				throw new Error(error.message || "Something went wrong");
			});
		}
		return response.json();
	}).catch(error => {
		console.error('Fetch error:', error);
		throw error;
	});
}

// Event listeners and handlers
document.addEventListener('DOMContentLoaded', () => {
	const token = localStorage.getItem('authToken');
	const username = localStorage.getItem('username');
	const userInfo = document.getElementById('user-info');
	const usernameElement = document.getElementById('username');

	if (token) {
		userInfo.style.display = 'block';
		usernameElement.innerText = username;
	} else {
		userInfo.style.display = 'none';
	}
});

document.getElementById('login-btn').addEventListener('click', () => {
	Swal.fire({
		title: 'Login',
		html: `
            <input id="login-username" class="swal2-input" placeholder="Username">
            <input id="login-password" class="swal2-input" type="password" placeholder="Password">
        `,
		confirmButtonText: 'Login',
		preConfirm: () => ({
			username: document.getElementById('login-username').value,
			password: document.getElementById('login-password').value
		})
	}).then(result => {
		if (result.isConfirmed) {
			fetch('http://localhost:8080/api/auth/login', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(result.value)
			}).then(handleFetchError)
				.then(data => {
					localStorage.setItem('authToken', data.token);
					localStorage.setItem('username', result.value.username);
					document.getElementById('username').innerText = result.value.username;
					document.getElementById('user-info').style.display = 'block';
					Swal.fire('Logged in successfully!');
					loadProducts();
					loadVendas();
				}).catch(error => {
					console.error('Error during login:', error);
					Swal.fire('Error', error.message, 'error');
				});
		}
	});
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
		preConfirm: () => ({
			username: document.getElementById('register-username').value,
			password: document.getElementById('register-password').value,
			email: document.getElementById('register-email').value,
			role: document.getElementById('register-role').value
		})
	}).then(result => {
		if (result.isConfirmed) {
			fetch('/api/auth/register', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
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

document.getElementById('logout-btn').addEventListener('click', () => {
	localStorage.removeItem('authToken');
	localStorage.removeItem('username');
	document.getElementById('user-info').style.display = 'none';
	Swal.fire('Logged out successfully!').then(() => {
		window.location.href = '/';
	});
});

// Sorting functions
function sortProducts(field) {
	if (currentSortField === field) {
		currentSortDirection = currentSortDirection === SORT_ASC ? SORT_DESC : SORT_ASC;
	} else {
		currentSortField = field;
		currentSortDirection = SORT_ASC;
	}
	loadProducts();
}

function sortVendas(field) {
	if (currentVendaSortField === field) {
		vendaSortDirection = vendaSortDirection === SORT_ASC ? SORT_DESC : SORT_ASC;
	} else {
		currentVendaSortField = field;
		vendaSortDirection = SORT_ASC;
	}
	loadVendas();
}

// Load data functions
function loadProducts() {
	fetchWithAuth("/api/produtos")
		.then(data => {
			const productList = document.getElementById("productList");
			productList.innerHTML = "";

			if (data.code === 404 || !Array.isArray(data)) {
				productList.innerHTML = `<tr><td colspan="5">${data.message || "Erro ao carregar produtos: formato de dados inesperado."}</td></tr>`;
				return;
			}

			if (data.length === 0) {
				productList.innerHTML = `<tr><td colspan="5">Nenhum produto encontrado.</td></tr>`;
			} else {
				data.sort((a, b) => (a[currentSortField] < b[currentSortField] ? -1 : a[currentSortField] > b[currentSortField] ? 1 : 0) * (currentSortDirection === SORT_ASC ? 1 : -1));
				productList.innerHTML = data.map(product => `
                    <tr>
                        <td>${product.id}</td>
                        <td>${product.nome}</td>
                        <td>${product.estoque}</td>
                        <td>${product.preco}</td>
                        <td>${product.ativo ? "Sim" : "Não"}</td>
                        <td>
                            <button onclick="editProduct(${JSON.stringify(product)})">Editar</button>
                            <button onclick="deleteProduct(${product.id})">Excluir</button>
                        </td>
                    </tr>
                `).join('');
			}
		})
		.catch(error => {
			console.error("Error loading products:", error);
			document.getElementById("productList").innerHTML = `<tr><td colspan="5">Erro ao carregar produtos: ${error.message}</td></tr>`;
		});
}

function loadVendas() {
	fetchWithAuth("/api/vendas")
		.then(data => {
			const vendaList = document.getElementById("vendaList");
			vendaList.innerHTML = "";

			if (data.code === 404 || !Array.isArray(data)) {
				vendaList.innerHTML = `<tr><td colspan="5">${data.message || "Erro ao carregar vendas: formato de dados inesperado."}</td></tr>`;
				return;
			}

			if (data.length === 0) {
				vendaList.innerHTML = `<tr><td colspan="5">Nenhuma venda encontrada.</td></tr>`;
			} else {
				data.sort((a, b) => (a[currentVendaSortField] < b[currentVendaSortField] ? -1 : a[currentVendaSortField] > b[currentVendaSortField] ? 1 : 0) * (vendaSortDirection === SORT_ASC ? 1 : -1));
				vendaList.innerHTML = data.map(venda => `
                    <tr>
                        <td>${venda.id}</td>
                        <td>${venda["id-produto"]}</td>
                        <td>${venda.quantidade}</td>
                        <td>${venda.dataVenda}</td>
                        <td>
                            <button onclick="editVenda(${JSON.stringify(venda)})">Editar</button>
                            <button onclick="deleteVenda(${venda.id})">Excluir</button>
                        </td>
                    </tr>
                `).join('');
			}
		})
		.catch(error => {
			console.log("Erro ao carregar vendas:", error);
			document.getElementById("vendaList").innerHTML = `<tr><td colspan="5">Erro ao carregar vendas: ${error.message}</td></tr>`;
		});
}

// CRUD functions for products
function editProduct(product) {
	document.getElementById("editProductId").value = product.id;
	document.getElementById("editProductName").value = product.nome;
	document.getElementById("editProductStock").value = product.estoque;
	document.getElementById("editProductPrice").value = product.preco;
	document.getElementById("editProductActive").checked = product.ativo;
}

function deleteProduct(productId) {
	if (confirm("Tem certeza que deseja excluir este produto?")) {
		fetchWithAuth(`/api/produtos/${productId}`, { method: "DELETE" })
			.then(handleFetchError)
			.then(() => {
				Swal.fire({
					icon: "success",
					title: "Produto Excluído",
					text: "Produto excluído com sucesso.",
				});
				loadProducts();
			})
			.catch(error => {
				Swal.fire({
					icon: "warning",
					title: "Produto Inativado",
					text: error.message,
				});
				loadProducts();
			});
	}
}

document.getElementById('createProductForm').addEventListener('submit', function(event) {
	event.preventDefault();

	const productName = document.getElementById('productName').value;
	const productStock = document.getElementById('productStock').value;
	const productPrice = document.getElementById('productPrice').value;

	const product = {
		nome: productName,
		estoque: parseInt(productStock, 10),
		preco: parseFloat(productPrice)
	};

	fetch('http://localhost:8080/api/produtos', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(product)
	})
		.then(response => response.json())
		.then(data => {
			const resultDiv = document.getElementById('createProductResult');
			if (data.code && data.status) {
				resultDiv.innerHTML = `<p style="color: red;">Erro: ${data.message}</p>`;
			} else {
				resultDiv.innerHTML = `<p>Produto criado com sucesso! ID: ${data.id}, Nome: ${data.nome}, Preço: ${data.preco}, Estoque: ${data.estoque}</p>`;
			}
			loadProducts();
		})
		.catch(error => {
			console.error('Erro ao enviar a requisição:', error);
			const resultDiv = document.getElementById('createProductResult');
			resultDiv.innerHTML = `<p style="color: red;">Erro ao enviar a requisição: ${error.message}</p>`;
		});
});

document.getElementById('editProductForm').addEventListener('submit', function(e) {
	e.preventDefault();
	const productId = document.getElementById("editProductId").value;
	const product = {
		nome: document.getElementById("editProductName").value,
		estoque: parseInt(document.getElementById("editProductStock").value, 10),
		preco: parseFloat(document.getElementById("editProductPrice").value),
		ativo: document.getElementById("editProductActive").checked
	};

	fetchWithAuth(`http://localhost:8080/api/produtos/${productId}`, {
		method: "PUT",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(product)
	}).then(response => {
		if (response.code && response.status) {
			document.getElementById("editProductResult").innerHTML = `<p style="color: red;">Erro: ${response.message}</p>`;
		} else {
			document.getElementById("editProductResult").innerHTML = `<p>Produto atualizado com sucesso! ID: ${response.id}, Nome: ${response.nome}, Preço: ${response.preco}, Estoque: ${response.estoque}</p>`;
			loadProducts(); // Atualiza a lista de produtos após a edição
		}
	}).catch(error => {
		console.error('Erro ao atualizar o produto:', error);
		document.getElementById("editProductResult").innerHTML = `<p style="color: red;">Erro ao atualizar o produto: ${error.message}</p>`;
	});
});

// CRUD functions for vendas
function editVenda(venda) {
	document.getElementById("editVendaId").value = venda.id;
	document.getElementById("editVendaProductId").value = venda["id-produto"];
	document.getElementById("editVendaQuantidade").value = venda.quantidade;
	document.getElementById("editVendaData").value = venda.dataVenda.substring(0, 16);
}

function deleteVenda(vendaId) {
	if (confirm("Tem certeza que deseja excluir esta venda?")) {
		fetchWithAuth(`/api/vendas/${vendaId}`, { method: "DELETE" })
			.then(handleFetchError)
			.then(() => {
				loadVendas();
			})
			.catch(error => {
				alert("Erro ao excluir venda: " + error.message);
			});
	}
}

document.getElementById('createVendaForm').addEventListener('submit', function(e) {
	e.preventDefault();
	const venda = {
		idProduto: document.getElementById("vendaProductId").value,
		quantidade: document.getElementById("vendaQuantidade").value,
		dataVenda: document.getElementById("vendaData").value + ":00Z"
	};

	fetchWithAuth("/api/vendas", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(venda)
	}).then(handleFetchError)
		.then(() => {
			document.getElementById("createVendaResult").innerText = "Venda criada com sucesso!";
			loadVendas();
			document.getElementById("vendaProductId").value = "";
			document.getElementById("vendaQuantidade").value = "";
			document.getElementById("vendaData").value = "";
		}).catch(error => {
			Swal.fire('Error', error.message, 'error');
		});
});

document.getElementById('editVendaForm').addEventListener('submit', function(e) {
	e.preventDefault();
	const venda = {
		id: document.getElementById("editVendaId").value,
		idProduto: document.getElementById("editVendaProductId").value,
		quantidade: document.getElementById("editVendaQuantidade").value,
		dataVenda: document.getElementById("editVendaData").value + ":00Z"
	};

	fetchWithAuth(`/api/vendas/${venda.id}`, {
		method: "PUT",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(venda)
	}).then(handleFetchError)
		.then(() => {
			document.getElementById("editVendaResult").innerText = "Venda atualizada com sucesso!";
			loadVendas();
		}).catch(error => {
			alert("Erro ao atualizar venda: " + error.message);
		});
});

document.getElementById("filterVendasForm").addEventListener("submit", function(e) {
	e.preventDefault();
	const startDate = document.getElementById("start-date").value + ":00Z";
	const endDate = document.getElementById("end-date").value + ":00Z";

	fetch(`/api/vendas/filtrar?startDate=${startDate}&endDate=${endDate}`)
		.then(handleFetchError)
		.then(data => {
			const filterVendasTableBody = document.querySelector("#filterVendasTable tbody");
			if (!filterVendasTableBody) {
				console.error("Elemento #filterVendasTable tbody não encontrado no DOM");
				return;
			}

			filterVendasTableBody.innerHTML = "";
			if (data.length === 0) {
				filterVendasTableBody.innerHTML = "<tr><td colspan='4'>Nenhuma venda encontrada no intervalo especificado.</td></tr>";
			} else {
				filterVendasTableBody.innerHTML = data.map(venda => `
                    <tr>
                        <td>${venda.id}</td>
                        <td>${venda["id-produto"]}</td>
                        <td>${venda.quantidade}</td>
                        <td>${venda.dataVenda}</td>
                    </tr>
                `).join('');
			}
		})
		.catch(error => {
			const errorMessage = `Erro ${error.message}`;
			alert(errorMessage);
			document.getElementById("filterVendasResult").innerText = errorMessage;
		});
});

// Report functions
let currentMonthlyReportSortField = "data";
let currentWeeklyReportSortField = "data";
let monthlyReportSortDirection = 1;
let weeklyReportSortDirection = 1;

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

function loadMonthlyReport() {
	const reportMonth = document.getElementById("reportMonth").value + "-01T00:00:00Z";

	fetch(`/api/vendas/relatorio/mensal?mesAno=${reportMonth}`)
		.then(handleFetchError)
		.then(data => {
			const monthlyReportList = document.getElementById("monthlyReportList");
			monthlyReportList.innerHTML = "";

			if (data.length === 0) {
				monthlyReportList.innerHTML = "<tr><td colspan='4'>Nenhum dado encontrado.</td></tr>";
			} else {
				data.sort((a, b) => (a[currentMonthlyReportSortField] < b[currentMonthlyReportSortField] ? -1 : a[currentMonthlyReportSortField] > b[currentMonthlyReportSortField] ? 1 : 0) * monthlyReportSortDirection);
				monthlyReportList.innerHTML = data.map(item => `
                    <tr>
                        <td>${item.id}</td>
                        <td>${item["id-produto"]}</td>
                        <td>${item.dataVenda}</td>
                        <td>${item.quantidade}</td>
                    </tr>
                `).join('');
			}
		})
		.catch(error => {
			console.error("There was a problem with the fetch operation:", error);
		});
}

function loadWeeklyReport() {
	const reportWeek = document.getElementById("reportWeek").value + ":00Z";

	fetch(`/api/vendas/relatorio/semanal?semana=${encodeURIComponent(reportWeek)}`)
		.then(handleFetchError)
		.then(data => {
			const weeklyReportList = document.getElementById("weeklyReportList");
			weeklyReportList.innerHTML = "";

			if (data.length === 0) {
				weeklyReportList.innerHTML = "<tr><td colspan='4'>Nenhum dado encontrado.</td></tr>";
			} else {
				data.sort((a, b) => (a[currentWeeklyReportSortField] < b[currentWeeklyReportSortField] ? -1 : a[currentWeeklyReportSortField] > b[currentWeeklyReportSortField] ? 1 : 0) * weeklyReportSortDirection);
				weeklyReportList.innerHTML = data.map(item => `
                    <tr>
                        <td>${item.id}</td>
                        <td>${item["id-produto"]}</td>
                        <td>${item.dataVenda}</td>
                        <td>${item.quantidade}</td>
                    </tr>
                `).join('');
			}
		})
		.catch(error => {
			console.error("Error:", error);
			const weeklyReportResult = document.getElementById("weeklyReportResult");
			if (weeklyReportResult) {
				weeklyReportResult.innerText = "Erro: " + error.message;
			}
		});
}

document.getElementById("monthlyReportForm").addEventListener("submit", function(e) {
	e.preventDefault();
	loadMonthlyReport();
});

document.getElementById("weeklyReportForm").addEventListener("submit", function(e) {
	e.preventDefault();
	loadWeeklyReport();
});

// Initial data loading
loadProducts();
loadVendas();