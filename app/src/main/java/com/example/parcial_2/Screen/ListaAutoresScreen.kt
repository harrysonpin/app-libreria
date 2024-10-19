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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.parcial_2.Model.Autores
import com.example.parcial_2.Repository.AutoresRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorListScreen(
    navController: NavHostController,
    autoresRepository: AutoresRepository
) {
    val scope = rememberCoroutineScope()
    var autores by remember { mutableStateOf(listOf<Autores>()) }
    var editandoAutor by remember { mutableStateOf<Autores?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        autores = withContext(Dispatchers.IO) { autoresRepository.getAllAutores() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Autores") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar nuevo autor")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(autores) { author ->
                AuthorListItem(
                    author = author,
                    onEdit = { editandoAutor = it },
                    onDelete = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                autoresRepository.eliminar(it)
                                autores = autoresRepository.getAllAutores()
                            }
                        }
                    },
                    onViewDetails = { navController.navigate("author_detail/${it.autor_id}") }
                )
            }
        }
    }

    if (editandoAutor != null) {
        EditAuthorDialog(
            author = editandoAutor!!,
            onDismiss = { editandoAutor = null },
            onSave = { updatedAuthor ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        autoresRepository.actualizar(updatedAuthor)
                        autores = autoresRepository.getAllAutores()
                    }
                    editandoAutor = null
                }
            },
            existingAuthors = autores
        )
    }

    if (showAddDialog) {
        AddAuthorDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newAuthor ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        autoresRepository.insertar(newAuthor)
                        autores = autoresRepository.getAllAutores()
                    }
                    showAddDialog = false
                }
            },
            existingAuthors = autores
        )
    }
}

@Composable
fun AuthorListItem(
    author: Autores,
    onEdit: (Autores) -> Unit,
    onDelete: (Autores) -> Unit,
    onViewDetails: (Autores) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = { onViewDetails(author) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${author.nombre} ${author.apellido}", style = MaterialTheme.typography.titleMedium)
                Text(text = author.nacionalidad, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onEdit(author) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar autor")
            }
            IconButton(onClick = { onDelete(author) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar autor")
            }
        }
    }
}

@Composable
fun EditAuthorDialog(
    author: Autores,
    onDismiss: () -> Unit,
    onSave: (Autores) -> Unit,
    existingAuthors: List<Autores>
) {
    var nombre by remember { mutableStateOf(author.nombre) }
    var apellido by remember { mutableStateOf(author.apellido) }
    var nacionalidad by remember { mutableStateOf(author.nacionalidad) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Autor") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = apellido.isBlank()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = nacionalidad,
                    onValueChange = { nacionalidad = it },
                    label = { Text("Nacionalidad") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nacionalidad.isBlank()
                )
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nombre.isBlank() || apellido.isBlank() || nacionalidad.isBlank()) {
                    errorMessage = "Todos los campos son obligatorios."
                } else if (existingAuthors.any { it.nombre == nombre && it.apellido == apellido && it.autor_id != author.autor_id }) {
                    errorMessage = "El autor ya existe."
                } else {
                    onSave(author.copy(nombre = nombre, apellido = apellido, nacionalidad = nacionalidad))
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddAuthorDialog(
    onDismiss: () -> Unit,
    onAdd: (Autores) -> Unit,
    existingAuthors: List<Autores>
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nuevo Autor") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = apellido.isBlank()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = nacionalidad,
                    onValueChange = { nacionalidad = it },
                    label = { Text("Nacionalidad") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nacionalidad.isBlank()
                )
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nombre.isBlank() || apellido.isBlank() || nacionalidad.isBlank()) {
                    errorMessage = "Todos los campos son obligatorios."
                } else if (existingAuthors.any { it.nombre == nombre && it.apellido == apellido }) {
                    errorMessage = "El autor ya existe."
                } else {
                    onAdd(Autores(nombre = nombre, apellido = apellido, nacionalidad = nacionalidad))
                }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}