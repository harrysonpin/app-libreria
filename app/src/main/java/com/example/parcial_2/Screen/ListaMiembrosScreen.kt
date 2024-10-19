package com.example.parcial_2.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.parcial_2.Model.User
import com.example.parcial_2.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListScreen(
    navController: NavHostController,
    userRepository: UserRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var users by remember { mutableStateOf(listOf<User>()) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        users = withContext(Dispatchers.IO) { userRepository.getAllUsers() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Miembros") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir nuevo miembro")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.navigate("author_list") },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Autores")
                    }
                    Button(
                        onClick = { navController.navigate("listaLibrosScreen") },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Prestamos")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(users) { user ->
                MemberListItem(
                    user = user,
                    onEdit = { editingUser = it },
                    onDelete = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                userRepository.eliminar(it)
                                users = userRepository.getAllUsers()
                            }
                            SnackbarHostState().showSnackbar("Miembro eliminado")
                        }
                    },
                    onViewLoans = { navController.navigate("member_detail/${it.miembro_id}") }
                )
            }
        }
    }

    if (editingUser != null) {
        EditMemberDialog(
            user = editingUser!!,
            onDismiss = { editingUser = null },
            onSave = { updatedUser ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        userRepository.actualizar(updatedUser)
                        users = userRepository.getAllUsers()
                    }
                    SnackbarHostState().showSnackbar("Miembro actualizado")
                    editingUser = null
                }
            },
            users = users
        )
    }

    if (showAddDialog) {
        AddMemberDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newUser ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        userRepository.insertar(newUser)
                        users = userRepository.getAllUsers()
                    }
                    SnackbarHostState().showSnackbar("Miembro añadido")
                    showAddDialog = false
                }
            },
            users = users
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListItem(
    user: User,
    onEdit: (User) -> Unit,
    onDelete: (User) -> Unit,
    onViewLoans: (User) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Miembro: ${user.nombre} ${user.apellido}"
            },
        onClick = { onViewLoans(user) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${user.nombre} ${user.apellido}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Fecha de inscripción: ${user.fecha_inscripcion}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onEdit(user) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar miembro")
            }
            IconButton(onClick = { onDelete(user) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar miembro")
            }
        }
    }
}

@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onAdd: (User) -> Unit,
    users: List<User>
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fechaInscripcion by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Nuevo Miembro") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    isError = nombre.isBlank()
                )
                TextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    isError = apellido.isBlank()
                )
                TextField(
                    value = fechaInscripcion,
                    onValueChange = { fechaInscripcion = it },
                    label = { Text("Fecha de inscripción") }
                )
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nombre.isBlank() || apellido.isBlank()) {
                    errorMessage = "Nombre y Apellido no pueden estar vacíos"
                } else if (users.any { it.nombre == nombre && it.apellido == apellido }) {
                    errorMessage = "El miembro ya existe"
                } else {
                    onAdd(User(nombre = nombre, apellido = apellido, fecha_inscripcion = fechaInscripcion))
                }
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditMemberDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit,
    users: List<User>
) {
    var nombre by remember { mutableStateOf(user.nombre) }
    var apellido by remember { mutableStateOf(user.apellido) }
    var fechaInscripcion by remember { mutableStateOf(user.fecha_inscripcion) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Miembro") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    isError = nombre.isBlank()
                )
                TextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    isError = apellido.isBlank()
                )
                TextField(
                    value = fechaInscripcion,
                    onValueChange = { fechaInscripcion = it },
                    label = { Text("Fecha de inscripción") }
                )
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nombre.isBlank() || apellido.isBlank()) {
                    errorMessage = "Nombre y Apellido no pueden estar vacíos"
                } else if (users.any { it.nombre == nombre && it.apellido == apellido && it.miembro_id != user.miembro_id }) {
                    errorMessage = "El miembro ya existe"
                } else {
                    onSave(user.copy(nombre = nombre, apellido = apellido, fecha_inscripcion = fechaInscripcion))
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}