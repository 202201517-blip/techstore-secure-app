package com.example.techstoreapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techstoreapp.data.local.SessionManager
import com.example.techstoreapp.data.model.CartItemRequest
import com.example.techstoreapp.data.model.CartItemResponse
import com.example.techstoreapp.data.model.CartSummaryResponse
import com.example.techstoreapp.data.model.CategoryResponse
import com.example.techstoreapp.data.model.ProductRequest
import com.example.techstoreapp.data.model.ProductResponse
import com.example.techstoreapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.text.DecimalFormat

private fun formatCurrency(value: Double): String {
    return "$" + DecimalFormat("#,##0.00").format(value)
}

private fun productEmoji(categoryName: String?, productName: String): String {
    val text = "${categoryName.orEmpty()} $productName".lowercase()

    return when {
        text.contains("celular") || text.contains("phone") || text.contains("smartphone") -> "📱"
        text.contains("laptop") || text.contains("computadora") || text.contains("pc") -> "💻"
        text.contains("audio") || text.contains("audífono") || text.contains("audifono") || text.contains("sony") -> "🎧"
        text.contains("watch") || text.contains("reloj") || text.contains("smartwatch") -> "⌚"
        text.contains("accesorio") -> "🔌"
        else -> "🛍️"
    }
}

@Composable
fun CatalogScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var products by remember { mutableStateOf<List<ProductResponse>>(emptyList()) }
    var categories by remember { mutableStateOf<List<CategoryResponse>>(emptyList()) }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showProductForm by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductResponse?>(null) }
    var showCartScreen by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }

    val role = sessionManager.getRole() ?: "UNKNOWN"
    val email = sessionManager.getEmail() ?: "Usuario"
    val bearerToken = sessionManager.getBearerToken()

    fun loadCatalog() {
        if (bearerToken == null) {
            message = "No hay sesión activa."
            return
        }

        coroutineScope.launch {
            isLoading = true
            message = "Cargando catálogo..."

            try {
                val productsResponse = RetrofitClient.productApi.getProducts(bearerToken)
                val categoriesResponse = RetrofitClient.productApi.getCategories(bearerToken)

                if (productsResponse.isSuccessful) {
                    products = productsResponse.body() ?: emptyList()
                } else {
                    message = "Error al cargar productos: ${productsResponse.code()}"
                }

                if (categoriesResponse.isSuccessful) {
                    categories = categoriesResponse.body() ?: emptyList()
                }

                if (productsResponse.isSuccessful) {
                    message = if (products.isEmpty()) {
                        "No hay productos disponibles."
                    } else {
                        ""
                    }
                }
            } catch (e: Exception) {
                message = "No se pudo conectar con la API: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteProduct(productId: Long) {
        if (bearerToken == null) {
            message = "No hay sesión activa."
            return
        }

        coroutineScope.launch {
            isLoading = true
            message = "Eliminando producto..."

            try {
                val response = RetrofitClient.productApi.deleteProduct(
                    token = bearerToken,
                    productId = productId
                )

                if (response.isSuccessful) {
                    message = "Producto eliminado correctamente."
                    loadCatalog()
                } else {
                    message = "No se pudo eliminar. Error ${response.code()}."
                }
            } catch (e: Exception) {
                message = "No se pudo conectar con la API: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addProductToCart(product: ProductResponse) {
        if (bearerToken == null) {
            message = "No hay sesión activa."
            return
        }

        coroutineScope.launch {
            isLoading = true
            message = "Agregando producto al carrito..."

            try {
                val response = RetrofitClient.cartApi.addItem(
                    token = bearerToken,
                    request = CartItemRequest(
                        productId = product.id,
                        quantity = 1
                    )
                )

                if (response.isSuccessful) {
                    message = "Producto agregado al carrito correctamente."
                } else {
                    message = "No se pudo agregar al carrito. Error ${response.code()}."
                }
            } catch (e: Exception) {
                message = "No se pudo conectar con la API: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCatalog()
    }

    if (showCartScreen) {
        CartScreen(
            token = bearerToken,
            onBack = {
                showCartScreen = false
                loadCatalog()
            }
        )
        return
    }

    if (showProductForm) {
        ProductFormScreen(
            token = bearerToken,
            categories = categories,
            productToEdit = productToEdit,
            onCancel = {
                showProductForm = false
                productToEdit = null
            },
            onProductSaved = {
                showProductForm = false
                productToEdit = null
                loadCatalog()
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "TechStore",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (role == "ADMIN") {
                        "Panel de administración"
                    } else {
                        "Catálogo de productos"
                    },
                    color = Color(0xFF6B7280)
                )
            }

            Box {
                TextButton(
                    onClick = {
                        showUserMenu = true
                    }
                ) {
                    Text(
                        text = "👤",
                        fontSize = 28.sp
                    )
                }

                DropdownMenu(
                    expanded = showUserMenu,
                    onDismissRequest = {
                        showUserMenu = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Usuario: $email")
                        },
                        onClick = {}
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Rol: $role")
                        },
                        onClick = {}
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Cerrar sesión")
                        },
                        onClick = {
                            showUserMenu = false
                            onLogout()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    loadCatalog()
                }
            ) {
                Text("Actualizar")
            }

            if (role == "ADMIN") {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        productToEdit = null
                        showProductForm = true
                    }
                ) {
                    Text("Agregar")
                }
            } else {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showCartScreen = true
                    }
                ) {
                    Text("Carrito")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Text("Procesando...")
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (message.contains("correctamente")) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            gridItems(products) { product ->
                ProductCard(
                    product = product,
                    role = role,
                    onAddToCart = {
                        addProductToCart(product)
                    },
                    onEdit = {
                        productToEdit = product
                        showProductForm = true
                    },
                    onDelete = {
                        deleteProduct(product.id)
                    }
                )
            }
        }
    }
}

@Composable
fun CartScreen(
    token: String?,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var cart by remember { mutableStateOf<CartSummaryResponse?>(null) }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var paymentStep by remember { mutableStateOf("cart") }
    var selectedPaymentMethod by remember { mutableStateOf("") }

    fun loadCart() {
        if (token == null) {
            message = "No hay sesión activa."
            return
        }

        coroutineScope.launch {
            isLoading = true
            message = "Cargando carrito..."

            try {
                val response = RetrofitClient.cartApi.getCart(token)

                if (response.isSuccessful) {
                    cart = response.body()
                    message = ""
                } else {
                    message = "Error al cargar carrito: ${response.code()}"
                }
            } catch (e: Exception) {
                message = "No se pudo conectar con la API: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateCartItem(item: CartItemResponse, newQuantity: Int) {
        if (token == null || newQuantity < 1) {
            return
        }

        coroutineScope.launch {
            isLoading = true
            message = "Actualizando carrito..."

            try {
                val response = RetrofitClient.cartApi.updateItem(
                    token = token,
                    itemId = item.id,
                    request = CartItemRequest(
                        productId = item.productId,
                        quantity = newQuantity
                    )
                )

                if (response.isSuccessful) {
                    cart = response.body()
                    message = ""
                } else {
                    message = "No se pudo actualizar. Error ${response.code()}."
                }
            } catch (e: Exception) {
                message = "No se pudo conectar con la API: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun removeCartItem(item: CartItemResponse) {
        if (token == null) {
            return
        }

        coroutineScope.launch {
            isLoading = true
            message = "Eliminando producto..."

            try {
                val response = RetrofitClient.cartApi.removeItem(
                    token = token,
                    itemId = item.id
                )

                if (response.isSuccessful) {
                    cart = response.body()
                    message = "Producto eliminado del carrito."
                } else {
                    message = "No se pudo eliminar. Error ${response.code()}."
                }
            } catch (e: Exception) {
                message = "No se pudo conectar con la API: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearCart(onSuccess: (() -> Unit)? = null) {
        if (token == null) {
            return
        }

        coroutineScope.launch {
            isLoading = true
            message = "Procesando..."

            try {
                val response = RetrofitClient.cartApi.clearCart(token)

                if (response.isSuccessful) {
                    cart = response.body()
                    message = ""
                    onSuccess?.invoke()
                } else {
                    message = "No se pudo vaciar el carrito. Error ${response.code()}."
                }
            } catch (e: Exception) {
                message = "No se pudo conectar con la API: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCart()
    }

    val currentCart = cart

    when (paymentStep) {
        "method" -> {
            PaymentMethodScreen(
                cart = currentCart,
                onBack = {
                    paymentStep = "cart"
                },
                onSelectMethod = { method ->
                    selectedPaymentMethod = method
                    paymentStep = "details"
                }
            )
            return
        }

        "details" -> {
            PaymentDetailsScreen(
                cart = currentCart,
                paymentMethod = selectedPaymentMethod,
                isLoading = isLoading,
                onBack = {
                    paymentStep = "method"
                },
                onPay = {
                    clearCart(
                        onSuccess = {
                            paymentStep = "success"
                        }
                    )
                }
            )
            return
        }

        "success" -> {
            PaymentSuccessScreen(
                onBackToCatalog = onBack
            )
            return
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Mi carrito",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Gestiona tus productos agregados",
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    loadCart()
                }
            ) {
                Text("Actualizar")
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onBack
            ) {
                Text("Volver")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Text("Procesando...")
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (message.contains("correctamente") || message.contains("eliminado")) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (currentCart == null || currentCart.items.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F4F6)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🛒", fontSize = 42.sp)
                    Text("Tu carrito está vacío.")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentCart.items) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = {
                            updateCartItem(item, item.quantity + 1)
                        },
                        onDecrease = {
                            updateCartItem(item, item.quantity - 1)
                        },
                        onRemove = {
                            removeCartItem(item)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEDE9FE)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen",
                        fontWeight = FontWeight.Bold
                    )

                    Text("Productos: ${currentCart.totalItems}")

                    Text(
                        text = "Total: ${formatCurrency(currentCart.totalAmount)}",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            paymentStep = "method"
                        }
                    ) {
                        Text("Proceder al pago")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            clearCart()
                        }
                    ) {
                        Text("Vaciar carrito")
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodScreen(
    cart: CartSummaryResponse?,
    onBack: () -> Unit,
    onSelectMethod: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Método de pago",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Selecciona cómo deseas pagar tu pedido",
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEDE9FE)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumen del pedido",
                    fontWeight = FontWeight.Bold
                )

                Text("Productos: ${cart?.totalItems ?: 0}")
                Text("Total a pagar: ${formatCurrency(cart?.totalAmount ?: 0.0)}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PaymentMethodCard(
            icon = "💳",
            title = "Tarjeta de crédito o débito",
            description = "Paga de forma segura con los datos de tu tarjeta.",
            onClick = {
                onSelectMethod("Tarjeta")
            }
        )

        PaymentMethodCard(
            icon = "🏦",
            title = "Transferencia bancaria",
            description = "Registra el banco y número de referencia.",
            onClick = {
                onSelectMethod("Transferencia")
            }
        )

        PaymentMethodCard(
            icon = "💵",
            title = "Pago contra entrega",
            description = "Paga en efectivo al recibir tu pedido.",
            onClick = {
                onSelectMethod("Contra entrega")
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack
        ) {
            Text("Volver al carrito")
        }
    }
}

@Composable
fun PaymentMethodCard(
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEDE9FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 28.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Button(
                onClick = onClick
            ) {
                Text("Elegir")
            }
        }
    }
}

@Composable
fun PaymentDetailsScreen(
    cart: CartSummaryResponse?,
    paymentMethod: String,
    isLoading: Boolean,
    onBack: () -> Unit,
    onPay: () -> Unit
) {
    var cardName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    var bankName by remember { mutableStateOf("") }
    var referenceNumber by remember { mutableStateOf("") }

    var receiverName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }

    fun validatePayment(): Boolean {
        if (paymentMethod == "Tarjeta") {
            if (cardName.isBlank()) {
                message = "Ingresa el nombre del titular."
                return false
            }

            if (cardNumber.length < 12) {
                message = "Ingresa un número de tarjeta válido."
                return false
            }

            if (expirationDate.isBlank()) {
                message = "Ingresa la fecha de vencimiento."
                return false
            }

            if (cvv.length < 3) {
                message = "Ingresa un CVV válido."
                return false
            }
        }

        if (paymentMethod == "Transferencia") {
            if (bankName.isBlank()) {
                message = "Ingresa el nombre del banco."
                return false
            }

            if (referenceNumber.isBlank()) {
                message = "Ingresa el número de referencia."
                return false
            }
        }

        if (paymentMethod == "Contra entrega") {
            if (receiverName.isBlank()) {
                message = "Ingresa el nombre de quien recibe."
                return false
            }

            if (address.isBlank()) {
                message = "Ingresa la dirección de entrega."
                return false
            }

            if (phone.isBlank()) {
                message = "Ingresa el teléfono de contacto."
                return false
            }
        }

        message = ""
        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Detalles del pago",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Método seleccionado: $paymentMethod",
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEDE9FE)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Total a pagar",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatCurrency(cart?.totalAmount ?: 0.0),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (paymentMethod == "Tarjeta") {
            OutlinedTextField(
                value = cardName,
                onValueChange = { cardName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre del titular") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Número de tarjeta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("MM/AA") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("CVV") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }

        if (paymentMethod == "Transferencia") {
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre del banco") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = referenceNumber,
                onValueChange = { referenceNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Número de referencia") },
                singleLine = true
            )
        }

        if (paymentMethod == "Contra entrega") {
            OutlinedTextField(
                value = receiverName,
                onValueChange = { receiverName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre de quien recibe") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Dirección de entrega") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                if (validatePayment()) {
                    onPay()
                }
            }
        ) {
            Text(
                text = if (isLoading) {
                    "Procesando..."
                } else {
                    "Proceder al pago"
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack
        ) {
            Text("Volver")
        }
    }
}

@Composable
fun PaymentSuccessScreen(
    onBackToCatalog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✅",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Pago realizado correctamente",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Tu pedido fue procesado de forma exitosa."
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBackToCatalog
        ) {
            Text("Volver al catálogo")
        }
    }
}

@Composable
fun ProductFormScreen(
    token: String?,
    categories: List<CategoryResponse>,
    productToEdit: ProductResponse?,
    onCancel: () -> Unit,
    onProductSaved: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedCategory by remember(productToEdit?.id) {
        mutableStateOf<CategoryResponse?>(null)
    }

    var expandedCategoryMenu by remember { mutableStateOf(false) }

    var name by remember(productToEdit?.id) {
        mutableStateOf(productToEdit?.name ?: "")
    }

    var description by remember(productToEdit?.id) {
        mutableStateOf(productToEdit?.description ?: "")
    }

    var price by remember(productToEdit?.id) {
        mutableStateOf(productToEdit?.price?.toString() ?: "")
    }

    var stock by remember(productToEdit?.id) {
        mutableStateOf(productToEdit?.stock?.toString() ?: "")
    }

    var imageUrl by remember(productToEdit?.id) {
        mutableStateOf(productToEdit?.imageUrl ?: "")
    }

    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(categories, productToEdit) {
        if (productToEdit != null && selectedCategory == null) {
            selectedCategory = categories.firstOrNull { it.id == productToEdit.categoryId }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (productToEdit == null) "Agregar producto" else "Editar producto",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Completa la información del producto",
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    expandedCategoryMenu = true
                }
            ) {
                Text(
                    text = selectedCategory?.name ?: "Seleccionar categoría"
                )
            }

            DropdownMenu(
                expanded = expandedCategoryMenu,
                onDismissRequest = {
                    expandedCategoryMenu = false
                }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(category.name)
                        },
                        onClick = {
                            selectedCategory = category
                            expandedCategoryMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre del producto") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Descripción") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Imagen / URL") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                val category = selectedCategory
                val priceValue = price.toDoubleOrNull()
                val stockValue = stock.toIntOrNull()

                if (token == null) {
                    message = "No hay sesión activa."
                    return@Button
                }

                if (category == null) {
                    message = "Selecciona una categoría."
                    return@Button
                }

                if (name.isBlank()) {
                    message = "Ingresa el nombre del producto."
                    return@Button
                }

                if (priceValue == null || priceValue <= 0) {
                    message = "Ingresa un precio válido."
                    return@Button
                }

                if (stockValue == null || stockValue < 0) {
                    message = "Ingresa un stock válido."
                    return@Button
                }

                val request = ProductRequest(
                    categoryId = category.id,
                    name = name.trim(),
                    description = description.trim().ifBlank { null },
                    price = priceValue,
                    stock = stockValue,
                    imageUrl = imageUrl.trim().ifBlank { null }
                )

                coroutineScope.launch {
                    isLoading = true
                    message = "Guardando producto..."

                    try {
                        val response = if (productToEdit == null) {
                            RetrofitClient.productApi.createProduct(
                                token = token,
                                request = request
                            )
                        } else {
                            RetrofitClient.productApi.updateProduct(
                                token = token,
                                productId = productToEdit.id,
                                request = request
                            )
                        }

                        if (response.isSuccessful) {
                            onProductSaved()
                        } else {
                            message = "No se pudo guardar el producto. Error ${response.code()}."
                        }
                    } catch (e: Exception) {
                        message = "No se pudo conectar con la API: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        ) {
            Text(
                text = if (isLoading) "Guardando..." else "Guardar producto"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCancel
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
fun ProductCard(
    product: ProductResponse,
    role: String,
    onAddToCart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val emoji = productEmoji(product.categoryName, product.name)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFEDE9FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 42.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = product.categoryName ?: "Sin categoría",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatCurrency(product.price),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Stock: ${product.stock}",
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (role == "ADMIN") {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEdit
                ) {
                    Text("Editar")
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDelete
                ) {
                    Text("Eliminar")
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = product.stock > 0,
                    onClick = onAddToCart
                ) {
                    Text(
                        text = if (product.stock > 0) {
                            "Agregar"
                        } else {
                            "Sin stock"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItemResponse,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val emoji = productEmoji(null, item.productName)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F3FF)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 30.sp
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.productName,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = formatCurrency(item.unitPrice),
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Cantidad: ${item.quantity}")

            Text(
                text = "Subtotal: ${formatCurrency(item.subtotal)}",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDecrease,
                    enabled = item.quantity > 1
                ) {
                    Text("-")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onIncrease
                ) {
                    Text("+")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1.4f),
                    onClick = onRemove
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}