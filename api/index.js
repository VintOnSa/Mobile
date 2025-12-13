//Recordar los require son como las dependencias del requirements.txt en Django
const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');
const multer = require('multer');
const path = require('path');

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// ========== CONFIGURACIÓN MULTER ==========
// Se define el Path o ruta donde se van a guardar, en este caso un volumen persistente anclado al servivio de la Api en Railway
const volumePath = '/imagenes-juegos';

const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, volumePath);
  },
  //Le da un nombre unico a la imagen o el archivo de imagen
  filename: function (req, file, cb) {
    const uniqueName = Date.now() + '-' + Math.round(Math.random() * 1E9) + path.extname(file.originalname);
    cb(null, uniqueName);
  }
});

const upload = multer({ 
  storage: storage,
  limits: {
    fileSize: 5 * 1024 * 1024
  }
});
//Le da la ruta final al Multer para guardar la imagen
app.use('/imagenes-juegos', express.static(volumePath));
// ========== FIN CONFIGURACIÓN MULTER ==========

// Conexión a la base de datos
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: { rejectUnauthorized: false }
});

// Crea tabla para Juegos si no existe
const crearTabla = async () => {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS juegos (
      id SERIAL PRIMARY KEY,
      titulo VARCHAR(255) NOT NULL,
      publicador VARCHAR(255),
      precio INTEGER,
      stock INTEGER,
      descripcion TEXT,
      plataforma VARCHAR(100),
      genero VARCHAR(100),
      imagenurl VARCHAR(255) DEFAULT ''
    )
  `);
  console.log('Tabla juegos creada');
};
crearTabla();

// ENDPOINTS

// 1. GET /juegos - Obtiene todos los Juegos y los ordena por id
app.get('/juegos', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM juegos ORDER BY id');
    res.json(result.rows);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// 2. GET /juegos/{id} - Busca Juego por ID
app.get('/juegos/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query('SELECT * FROM juegos WHERE id = $1', [id]);
    
    //Error por si no encontro el juego
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Juego no encontrado' });
    }
    //catch en caso de error con el mensaje de error. Puede ser diferente al NotFound404
    res.json(result.rows[0]);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// 3. POST /juegos - Agrega Juego
app.post('/juegos', async (req, res) => {
  try {
    const { titulo, publicador, precio, stock, descripcion, plataforma, genero, imagenurl } = req.body;
    
    const result = await pool.query(
      `INSERT INTO juegos (titulo, publicador, precio, stock, descripcion, plataforma, genero, imagenurl) 
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8) RETURNING *`,
      [titulo, publicador, precio, stock, descripcion, plataforma, genero, imagenurl]
    );
    
    res.json(result.rows[0]);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// 4. PUT /juegos - Actualiza Juego con el id
app.put('/juegos', async (req, res) => {
  try {
    const { id, titulo, publicador, precio, stock, descripcion, plataforma, genero, imagenurl } = req.body;
    
    if (!id) {
      return res.status(400).json({ error: 'ID es requerido para actualizar' });
    }
    
    const result = await pool.query(
      `UPDATE juegos 
       SET titulo=$1, publicador=$2, precio=$3, stock=$4, descripcion=$5, plataforma=$6, genero=$7, imagenurl=$8 
       WHERE id=$9 RETURNING *`,
      [titulo, publicador, precio, stock, descripcion, plataforma, genero, imagenurl, id]
    );
    
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Juego no encontrado' });
    }
    
    res.json(result.rows[0]);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// 5. DELETE /juegos/{id} - Elimina Juego con id
app.delete('/juegos/:id', async (req, res) => {
  try {
    const { id } = req.params;
    
    const result = await pool.query('DELETE FROM juegos WHERE id = $1', [id]);
    
    if (result.rowCount === 0) {
      return res.status(404).json({ error: 'Juego no encontrado' });
    }
    
    res.json({ message: 'Juego eliminado correctamente' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// ========== ENDPOINT SUBIR IMAGEN ==========
app.post('/subir-imagen', upload.single('imagen'), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: 'No se subio ninguna imagen' });
    }
    
    const imageUrl = `https://${req.get('host')}/imagenes-juegos/${req.file.filename}`;// Recordar usar https o http
    res.json({ imageUrl });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.listen(process.env.PORT || 3000, () => {
  console.log('API de Juegos funciona bien god');
});