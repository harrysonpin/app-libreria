package com.example.parcial_2.Screen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial_2.Repository.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun navegador(
    userRepository: UserRepository,
    prestamosRepository: PrestamosRepository,
    autoresRepository: AutoresRepository,
    librosRepository: LibrosRepository
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "member_list") {
        composable("member_list") {
            MemberListScreen(navController, userRepository)
        }
        composable("member_detail/{memberId}") { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId")?.toInt() ?: 0
            MemberDetailScreen(navController, memberId, userRepository, prestamosRepository, librosRepository)
        }
        composable("author_list") {
            AuthorListScreen(navController, autoresRepository)
        }
        composable("author_detail/{authorId}") { backStackEntry ->
            val authorId = backStackEntry.arguments?.getString("authorId")?.toInt() ?: 0
            DetalleAutorScreen(navController, authorId, librosRepository)
        }
        composable("listaLibrosScreen") {
            AgregarPrestamoScreen(navController, prestamosRepository, librosRepository, userRepository)
        }
    }
}