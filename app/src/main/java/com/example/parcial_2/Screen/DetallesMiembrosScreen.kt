package com.example.parcial_2.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailScreen(
    navController: NavHostController,
    memberId: Int,
    userRepository: UserRepository,
    prestamosRepository: PrestamosRepository,
    librosRepository: LibrosRepository
) {
    val scope = rememberCoroutineScope()
    var member by remember { mutableStateOf<User?>(null) }
    var prestamos by remember { mutableStateOf(listOf<Prestamos>()) }

    LaunchedEffect(memberId) {
        member = withContext(Dispatchers.IO) { userRepository.getUserById(memberId) }
        prestamos = withContext(Dispatchers.IO) { prestamosRepository.getPrestamosByUser(memberId) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Miembro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
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
            member?.let { user ->
                item {
                    MemberInfoCard(user)
                }
            }

            item {
                Text(
                    "Préstamos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            items(prestamos) { prestamo ->
                LoanCard(prestamo, librosRepository) { prestamoToDelete ->
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            prestamosRepository.eliminar(prestamoToDelete)
                            prestamos = prestamosRepository.getPrestamosByUser(memberId)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MemberInfoCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${user.nombre} ${user.apellido}",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("ID: ${user.miembro_id}")
            Text("Fecha de registro: ${user.fecha_inscripcion}")
        }
    }
}

@Composable
fun LoanCard(prestamo: Prestamos, librosRepository: LibrosRepository, onDelete: (Prestamos) -> Unit) {
    var libro by remember { mutableStateOf<Libros?>(null) }

    LaunchedEffect(prestamo.libro_id) {
        libro = withContext(Dispatchers.IO) { librosRepository.getLibroById(prestamo.libro_id) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Préstamo ID: ${prestamo.Prestamo_id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            libro?.let {
                Text("Libro ID: ${prestamo.libro_id}")
                Text("Nombre del libro: ${it.titulo}")
                Text("Género del libro: ${it.genero}")
            } ?: Text("Cargando detalles del libro...")
            Text("Fecha de préstamo: ${formatDate(prestamo.fecha_prestamo)}")
            Text("Fecha de devolución: ${formatDate(prestamo.fecha_devolucion)}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onDelete(prestamo) },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar Préstamo")
            }
        }
    }
}
fun formatDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return try {
        val date = inputFormat.parse(dateString)
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}