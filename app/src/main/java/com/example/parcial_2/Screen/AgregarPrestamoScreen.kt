package com.example.parcial_2.Screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.parcial_2.Model.Libros
import com.example.parcial_2.Model.Prestamos
import com.example.parcial_2.Model.User
import com.example.parcial_2.Repository.LibrosRepository
import com.example.parcial_2.Repository.PrestamosRepository
import com.example.parcial_2.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPrestamoScreen(
    navController: NavHostController,
    prestamosRepository: PrestamosRepository,
    librosRepository: LibrosRepository,
    userRepository: UserRepository
) {
    val scope = rememberCoroutineScope()

    val currentDate = LocalDate.now()
    val dueDate = currentDate.plusMonths(2)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var fechaPrestamo by remember { mutableStateOf(currentDate.format(dateFormatter)) }
    var fechaDevolucion by remember { mutableStateOf(dueDate.format(dateFormatter)) }

    var libros by remember { mutableStateOf(listOf<Libros>()) }
    var users by remember { mutableStateOf(listOf<User>()) }
    var selectedLibro by remember { mutableStateOf<Libros?>(null) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        libros = withContext(Dispatchers.IO) { librosRepository.getAllLibros() }
        users = withContext(Dispatchers.IO) { userRepository.getAllUsers() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Préstamo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(SnackbarHostState()) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    "Seleccionar Libro",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                LibroSelectionItem(
                    libros = libros,
                    selectedLibro = selectedLibro,
                    onSelect = { selectedLibro = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Seleccionar Usuario",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                UserSelectionItem(
                    users = users,
                    selectedUser = selectedUser,
                    onSelect = { selectedUser = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (selectedLibro != null && selectedUser != null) {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    val prestamo = Prestamos(
                                        fecha_prestamo = fechaPrestamo,
                                        fecha_devolucion = fechaDevolucion,
                                        libro_id = selectedLibro!!.libro_id,
                                        miembro_id = selectedUser!!.miembro_id
                                    )
                                    prestamosRepository.insertar(prestamo)
                                }
                                snackbarMessage = "Préstamo agregado exitosamente"
                                showSnackbar = true
                                navController.popBackStack()
                            }
                        } else {
                            snackbarMessage = "Por favor, seleccione un libro y un usuario"
                            showSnackbar = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar Préstamo")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            kotlinx.coroutines.delay(3000)
            showSnackbar = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibroSelectionItem(
    libros: List<Libros>,
    selectedLibro: Libros?,
    onSelect: (Libros) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedLibro?.titulo ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            libros.forEach { libro ->
                DropdownMenuItem(
                    text = { Text(libro.titulo) },
                    onClick = {
                        onSelect(libro)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSelectionItem(
    users: List<User>,
    selectedUser: User?,
    onSelect: (User) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedUser?.let { "${it.nombre} ${it.apellido}" } ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            users.forEach { user ->
                DropdownMenuItem(
                    text = { Text("${user.nombre} ${user.apellido}") },
                    onClick = {
                        onSelect(user)
                        expanded = false
                    }
                )
            }
        }
    }
}