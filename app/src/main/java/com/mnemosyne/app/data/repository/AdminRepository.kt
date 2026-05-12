package com.mnemosyne.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.data.model.Obra
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.data.model.TipoEntrada
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class AdminRepository {

    private val db = FirebaseFirestore.getInstance()
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private fun DocumentSnapshot.getStringOrTimestamp(field: String): String {
        return when (val value = get(field)) {
            is String -> value
            is Timestamp -> sdf.format(value.toDate())
            is java.util.Date -> sdf.format(value)
            else -> ""
        }
    }

    // ── MUSEOS ────────────────────────────────────────────
    suspend fun obtenerMuseos(): FirebaseResult<Map<String, String>> {
        return try {
            val snapshot = db.collection("museos").get().await()
            val museos = snapshot.documents.associate { doc ->
                doc.id to (doc.getString("nombre") ?: doc.id)
            }
            FirebaseResult.Success(museos)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar museos")
        }
    }

    // ── EXPOSICIONES ──────────────────────────────────────
    suspend fun obtenerExposiciones(museoId: String): FirebaseResult<List<Exposicion>> {
        return try {
            val snapshot = db.collection("museos")
                .document(museoId)
                .collection("exposiciones")
                .get().await()
            val lista = snapshot.documents.map { doc ->
                val tiposRaw = doc.get("tiposEntrada") as? List<Map<String, Any>> ?: emptyList()
                val tipos = tiposRaw.map { t ->
                    TipoEntrada(
                        id = t["id"] as? String ?: "",
                        nombre = t["nombre"] as? String ?: "",
                        descripcion = t["descripcion"] as? String ?: "",
                        precio = (t["precio"] as? Number)?.toDouble() ?: 0.0
                    )
                }
                Exposicion(
                    id = doc.id,
                    titulo = doc.getString("titulo") ?: "",
                    descripcion = doc.getString("descripcion") ?: "",
                    fechaInicio = doc.getStringOrTimestamp("fechaInicio"),
                    fechaFin = doc.getStringOrTimestamp("fechaFin"),
                    imagenUrl = doc.getString("imagenUrl") ?: "",
                    destacada = doc.getBoolean("destacada") ?: false,
                    esPublica = doc.getBoolean("esPublica") ?: true,
                    museoNombre = doc.getString("museoNombre") ?: "",
                    museoId = museoId,
                    tiposEntrada = tipos
                )
            }
            FirebaseResult.Success(lista)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar exposiciones")
        }
    }

    suspend fun crearExposicion(museoId: String, exposicion: Exposicion): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "titulo" to exposicion.titulo,
                "descripcion" to exposicion.descripcion,
                "fechaInicio" to exposicion.fechaInicio,
                "fechaFin" to exposicion.fechaFin,
                "imagenUrl" to exposicion.imagenUrl,
                "destacada" to exposicion.destacada,
                "esPublica" to exposicion.esPublica,
                "museoNombre" to exposicion.museoNombre,
                "tiposEntrada" to exposicion.tiposEntrada.map { t ->
                    mapOf(
                        "id" to t.id,
                        "nombre" to t.nombre,
                        "descripcion" to t.descripcion,
                        "precio" to t.precio
                    )
                }
            )
            db.collection("museos").document(museoId)
                .collection("exposiciones").add(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al crear exposición")
        }
    }

    suspend fun actualizarExposicion(museoId: String, exposicion: Exposicion): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "titulo" to exposicion.titulo,
                "descripcion" to exposicion.descripcion,
                "fechaInicio" to exposicion.fechaInicio,
                "fechaFin" to exposicion.fechaFin,
                "imagenUrl" to exposicion.imagenUrl,
                "destacada" to exposicion.destacada,
                "esPublica" to exposicion.esPublica,
                "museoNombre" to exposicion.museoNombre,
                "tiposEntrada" to exposicion.tiposEntrada.map { t ->
                    mapOf(
                        "id" to t.id,
                        "nombre" to t.nombre,
                        "descripcion" to t.descripcion,
                        "precio" to t.precio
                    )
                }
            )
            db.collection("museos").document(museoId)
                .collection("exposiciones").document(exposicion.id)
                .update(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar exposición")
        }
    }

    suspend fun eliminarExposicion(museoId: String, exposicionId: String): FirebaseResult<Unit> {
        return try {
            db.collection("museos").document(museoId)
                .collection("exposiciones").document(exposicionId)
                .delete().await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al eliminar exposición")
        }
    }

    // ── NOTICIAS ──────────────────────────────────────────
    suspend fun obtenerNoticias(museoId: String): FirebaseResult<List<Noticia>> {
        return try {
            val snapshot = db.collection("museos")
                .document(museoId)
                .collection("noticias")
                .get().await()
            val lista = snapshot.documents.map { doc ->
                Noticia(
                    id = doc.id,
                    titulo = doc.getString("titulo") ?: "",
                    contenido = doc.getString("contenido") ?: "",
                    fecha = doc.getStringOrTimestamp("fecha"),
                    imagenUrl = doc.getString("imagenUrl") ?: "",
                    destacada = doc.getBoolean("destacada") ?: false,
                    museoNombre = doc.getString("museoNombre") ?: ""
                )
            }
            FirebaseResult.Success(lista)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar noticias")
        }
    }

    suspend fun crearNoticia(museoId: String, noticia: Noticia): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "titulo" to noticia.titulo,
                "contenido" to noticia.contenido,
                "fecha" to noticia.fecha,
                "imagenUrl" to noticia.imagenUrl,
                "destacada" to noticia.destacada,
                "museoNombre" to noticia.museoNombre
            )
            db.collection("museos").document(museoId)
                .collection("noticias").add(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al crear noticia")
        }
    }

    suspend fun actualizarNoticia(museoId: String, noticia: Noticia): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "titulo" to noticia.titulo,
                "contenido" to noticia.contenido,
                "fecha" to noticia.fecha,
                "imagenUrl" to noticia.imagenUrl,
                "destacada" to noticia.destacada,
                "museoNombre" to noticia.museoNombre
            )
            db.collection("museos").document(museoId)
                .collection("noticias").document(noticia.id)
                .update(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar noticia")
        }
    }

    suspend fun eliminarNoticia(museoId: String, noticiaId: String): FirebaseResult<Unit> {
        return try {
            db.collection("museos").document(museoId)
                .collection("noticias").document(noticiaId)
                .delete().await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al eliminar noticia")
        }
    }

    // ── OBRAS ─────────────────────────────────────────────
    suspend fun obtenerObras(museoId: String): FirebaseResult<List<Obra>> {
        return try {
            val snapshot = db.collection("museos")
                .document(museoId)
                .collection("obras")
                .get().await()
            val lista = snapshot.documents.map { doc ->
                Obra(
                    id = doc.id,
                    nombre = doc.getString("nombre") ?: "",
                    autor = doc.getString("autor") ?: "",
                    siglo = doc.getString("siglo") ?: "",
                    cultura = doc.getString("cultura") ?: "",
                    descripcion = doc.getString("descripcion") ?: "",
                    imagenUrl = doc.getString("imagenUrl") ?: "",
                    disponible = doc.getBoolean("disponible") ?: true
                )
            }
            FirebaseResult.Success(lista)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar obras")
        }
    }

    suspend fun crearObra(museoId: String, obra: Obra): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "nombre" to obra.nombre,
                "autor" to obra.autor,
                "siglo" to obra.siglo,
                "cultura" to obra.cultura,
                "descripcion" to obra.descripcion,
                "imagenUrl" to obra.imagenUrl,
                "disponible" to obra.disponible
            )
            db.collection("museos").document(museoId)
                .collection("obras").add(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al crear obra")
        }
    }

    suspend fun actualizarObra(museoId: String, obra: Obra): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "nombre" to obra.nombre,
                "autor" to obra.autor,
                "siglo" to obra.siglo,
                "cultura" to obra.cultura,
                "descripcion" to obra.descripcion,
                "imagenUrl" to obra.imagenUrl,
                "disponible" to obra.disponible
            )
            db.collection("museos").document(museoId)
                .collection("obras").document(obra.id)
                .update(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar obra")
        }
    }

    suspend fun eliminarObra(museoId: String, obraId: String): FirebaseResult<Unit> {
        return try {
            db.collection("museos").document(museoId)
                .collection("obras").document(obraId)
                .delete().await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al eliminar obra")
        }
    }

    // ── STOCK ─────────────────────────────────────────────
    suspend fun obtenerStock(museoId: String): FirebaseResult<List<Stock>> {
        return try {
            val snapshot = db.collection("museos")
                .document(museoId)
                .collection("stock")
                .get().await()
            val lista = snapshot.documents.map { doc ->
                Stock(
                    id = doc.id,
                    nombreProducto = doc.getString("nombreProducto") ?: "",
                    precio = doc.getDouble("precio") ?: 0.0,
                    cantidad = doc.getLong("cantidad")?.toInt() ?: 0,
                    imagenUrl = doc.getString("imagenUrl") ?: "",
                    museoId = museoId
                )
            }
            FirebaseResult.Success(lista)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar stock")
        }
    }

    suspend fun crearProducto(museoId: String, producto: Stock): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "nombreProducto" to producto.nombreProducto,
                "precio" to producto.precio,
                "cantidad" to producto.cantidad,
                "imagenUrl" to producto.imagenUrl
            )
            db.collection("museos").document(museoId)
                .collection("stock").add(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al crear producto")
        }
    }

    suspend fun actualizarProducto(museoId: String, producto: Stock): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "nombreProducto" to producto.nombreProducto,
                "precio" to producto.precio,
                "cantidad" to producto.cantidad,
                "imagenUrl" to producto.imagenUrl
            )
            db.collection("museos").document(museoId)
                .collection("stock").document(producto.id)
                .update(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar producto")
        }
    }

    suspend fun eliminarProducto(museoId: String, productoId: String): FirebaseResult<Unit> {
        return try {
            db.collection("museos").document(museoId)
                .collection("stock").document(productoId)
                .delete().await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al eliminar producto")
        }
    }
}