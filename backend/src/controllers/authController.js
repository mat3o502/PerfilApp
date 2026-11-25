const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const pool = require('../config/db');

// Registro de un nuevo usuario
exports.registrar = async (req, res) => {
  try {
    const { nombre, apellido, correo, password } = req.body || {};

    if (!nombre || !apellido || !correo || !password) {
      return res.status(400).json({ mensaje: 'Todos los campos son obligatorios' });
    }
    if (password.length < 6) {
      return res.status(400).json({ mensaje: 'La contrasena debe tener al menos 6 caracteres' });
    }

    // Verificar que el correo no este registrado
    const [existe] = await pool.query('SELECT id FROM usuarios WHERE correo = ?', [correo]);
    if (existe.length > 0) {
      return res.status(409).json({ mensaje: 'El correo ya esta registrado' });
    }

    // Encriptar la contrasena antes de guardarla
    const passwordHash = await bcrypt.hash(password, 10);

    await pool.query(
      'INSERT INTO usuarios (nombre, apellido, correo, password_hash) VALUES (?, ?, ?, ?)',
      [nombre, apellido, correo, passwordHash]
    );

    res.status(201).json({ mensaje: 'Usuario registrado correctamente' });
  } catch (error) {
    res.status(500).json({ mensaje: 'Error en el servidor', detalle: error.message });
  }
};

// Inicio de sesion
exports.login = async (req, res) => {
  try {
    const { correo, password } = req.body || {};

    if (!correo || !password) {
      return res.status(400).json({ mensaje: 'Correo y contrasena son obligatorios' });
    }

    const [filas] = await pool.query('SELECT * FROM usuarios WHERE correo = ?', [correo]);
    if (filas.length === 0) {
      return res.status(401).json({ mensaje: 'Credenciales incorrectas' });
    }

    const usuario = filas[0];

    // Comparar la contrasena escrita con el hash guardado
    const coincide = await bcrypt.compare(password, usuario.password_hash);
    if (!coincide) {
      return res.status(401).json({ mensaje: 'Credenciales incorrectas' });
    }

    // Generar el token de sesion (dura 1 dia)
    const token = jwt.sign(
      { id: usuario.id, correo: usuario.correo },
      process.env.JWT_SECRET,
      { expiresIn: '1d' }
    );

    res.json({
      mensaje: 'Login exitoso',
      token,
      usuario: {
        id: usuario.id,
        nombre: usuario.nombre,
        apellido: usuario.apellido,
        correo: usuario.correo
      }
    });
  } catch (error) {
    res.status(500).json({ mensaje: 'Error en el servidor', detalle: error.message });
  }
};

// Datos del perfil del usuario autenticado
exports.perfil = async (req, res) => {
  try {
    const [filas] = await pool.query(
      'SELECT id, nombre, apellido, correo, fecha_registro FROM usuarios WHERE id = ?',
      [req.usuario.id]
    );

    if (filas.length === 0) {
      return res.status(404).json({ mensaje: 'Usuario no encontrado' });
    }

    res.json(filas[0]);
  } catch (error) {
    res.status(500).json({ mensaje: 'Error en el servidor', detalle: error.message });
  }
};