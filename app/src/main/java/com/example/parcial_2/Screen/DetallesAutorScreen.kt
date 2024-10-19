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
import com.example.parcial_2.Model.Libros
import com.example.parcial_2.Repository.LibrosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAutorScreen(
    navController: NavHostController,
    authorId: Int,
    librosRepository: LibrosRepository
) {
    val scope = rememberCoroutineScope()
    var libros by remember { mutableStateOf(listOf<Libros>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Libros?>(null) }

    LaunchedEffect(authorId) {
        libros = withContext(Dispatchers.IO) { librosRepository.getLibrosByAutor(authorId) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Autor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar nuevo libro")
                    }
                }
            )
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
                    "Libros del Autor",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            items(libros) { libro ->
                LibroCard(
                    libro = libro,
                    onDelete = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                librosRepository.eliminar(libro)
                                libros = librosRepository.getLibrosByAutor(authorId)
                            }
                        }
                    },
                    onEdit = { libroToEdit ->
                        showEditDialog = libroToEdit
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AgregarLibroDialog(
            authorId = authorId,
            onDismiss = { showAddDialog = false },
            onAdd = { nuevoLibro ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        librosRepository.insertar(nuevoLibro)
                        libros = librosRepository.getLibrosByAutor(authorId)
                    }
                    showAddDialog = false
                }
            }
        )
    }

    showEditDialog?.let { libro ->
        EditarLibroDialog(
            libro = libro,
            onDismiss = { showEditDialog = null },
            onEdit = { libroEditado ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        librosRepository.updateLibro(libroEditado)
                        libros = librosRepository.getLibrosByAutor(authorId)
                    }
                    showEditDialog = null
                }
            }
        )
    }
}

@Composable
fun LibroCard(libro: Libros, onDelete: () -> Unit, onEdit: (Libros) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Género: ${libro.genero}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar Libro")
            }
            IconButton(onClick = { onEdit(libro) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Libro")
            }
        }
    }
}
@Composable
fun AgregarLibroDialog(
    authorId: Int,
    onDismiss: () -> Unit,
    onAdd: (Libros) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nuevo Libro") },
        text = {
            Column {
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Género") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (titulo.isNotBlank() && genero.isNotBlank()) {
                    onAdd(Libros(titulo = titulo, autor_id = authorId, genero = genero))
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
@Composable
fun EditarLibroDialog(
    libro: Libros,
    onDismiss: () -> Unit,
    onEdit: (Libros) -> Unit
) {
    var titulo by remember { mutableStateOf(libro.titulo) }
    var genero by remember { mutableStateOf(libro.genero) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Libro") },
        text = {
            Column {
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Género") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (titulo.isNotBlank() && genero.isNotBlank()) {
                    onEdit(libro.copy(titulo = titulo, genero = genero))
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