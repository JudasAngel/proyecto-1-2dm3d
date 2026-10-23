package com.example.login.ui.pantailak

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apparrese.R

private val ArreseGorria = Color(0xFFEF343B)
private val GrisSuave = Color(0xFFF5F5F5)
private val TextoGris = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen() {
    var menuAbierto by remember { mutableStateOf(false) }
    var cantidad by remember { mutableStateOf(1) }
    val precioUnitario = 2.40
    val total = precioUnitario * cantidad

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(96.dp),
                title = {
                    Image(
                        painter = painterResource(R.drawable.logoarrese2),
                        contentDescription = "Logo Arrese",
                        modifier = Modifier.height(65.dp)
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menú",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Opción provisional") },
                                onClick = { menuAbierto = false }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArreseGorria)
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(64.dp),
                containerColor = ArreseGorria,
                windowInsets = WindowInsets(0.dp)
            ) {
                NavigationBarItem(
                    selected = false, onClick = {},
                    icon = { Image(painter = painterResource(R.drawable.home), contentDescription = "Inicio", modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(Color.White)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, unselectedIconColor = Color.White, indicatorColor = ArreseGorria)
                )
                NavigationBarItem(
                    selected = true, onClick = {},
                    icon = { Image(painter = painterResource(R.drawable.shoppingcart), contentDescription = "Productos", modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(Color.White)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, unselectedIconColor = Color.White, indicatorColor = ArreseGorria)
                )
                NavigationBarItem(
                    selected = false, onClick = {},
                    icon = { Image(painter = painterResource(R.drawable.perfil), contentDescription = "Perfil", modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(Color.White)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, unselectedIconColor = Color.White, indicatorColor = ArreseGorria)
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // 1. Imagen del producto con fondo gris suave y esquinas redondeadas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(GrisSuave, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Imagen del producto", fontSize = 14.sp, color = TextoGris)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 2. Título con fuente Serif
            Text(
                text = "Gurinezko\nopila",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Precio unitario
            Text(
                text = "2,40 €",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Línea divisoria fina gris
            HorizontalDivider(
                color = Color.LightGray.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Descripción
            Text(
                text = "Betiko klasikoa: opil samur eta harroa, " +
                        "gurin-krema tradizionalarekin betea. " +
                        "Bilboko benetako zaporea.",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = TextoGris
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 6. Total alineado a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Totala: ",
                    fontSize = 16.sp,
                    color = TextoGris
                )
                Text(
                    text = "%.2f€".format(total).replace(".", ","),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Fila con el selector de cantidad + botón
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Selector de cantidad mejorado
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GrisSuave,
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        // Botón restar
                        IconButton(
                            onClick = { if (cantidad > 1) cantidad-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text(
                                text = "−",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (cantidad > 1) ArreseGorria else Color.LightGray
                            )
                        }

                        // Número
                        Text(
                            text = cantidad.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.width(28.dp),
                            textAlign = TextAlign.Center
                        )

                        // Botón sumar
                        IconButton(
                            onClick = { cantidad++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text(
                                text = "+",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ArreseGorria
                            )
                        }
                    }
                }

                // Botón "Saskira gehitu" ocupando el resto del ancho
                Button(
                    onClick = { /* Acción de añadir al carrito con cantidad */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArreseGorria),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Saskira gehitu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "DetalleProductoScreen"
)
@Composable
fun DetalleProductoScreenPreview() {
    DetalleProductoScreen()
}