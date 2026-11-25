require('dotenv').config();
const express = require('express');
const cors = require('cors');
const pool = require('./config/db');

const app = express();

app.use(cors());
app.use(express.json());
const authRoutes = require('./routes/authRoutes');
app.use('/api/auth', authRoutes);

app.get('/', async (req, res) => {
  try {
    await pool.query('SELECT 1');
    res.json({ mensaje: 'Backend funcionando', baseDatos: 'conectada' });
  } catch (error) {
    res.status(500).json({
      mensaje: 'Error de conexión a la base de datos',
      detalle: error.message
    });
  }
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, '0.0.0.0', () => {
  console.log('Servidor corriendo en el puerto ' + PORT);
});