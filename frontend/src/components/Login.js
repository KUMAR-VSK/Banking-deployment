import React, { useState } from 'react';
import api from '../api';

function Login({ onLogin }) {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    if (error) setError('');
    if (success) setSuccess('');
  };

  const validateForm = () => {
    if (formData.username.length < 3) {
      setError('Username must be at least 3 characters long');
      return false;
    }
    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await api.post('/auth/login', formData);

      setSuccess('Login successful! Redirecting...');
      onLogin(response.data);
    } catch (err) {
      if (err.response) {
        // Server responded with error status
        const errorData = err.response.data;
        setError(errorData.error || errorData.message || 'Login failed');
      } else if (err.request) {
        // Network error
        setError('Cannot connect to server. Please check your connection and try again.');
      } else {
        // Other error
        setError('An unexpected error occurred. Please try again later.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-header">
        <div className="auth-icon">🔐</div>
        <h2 className="auth-title">Login</h2>
        <p className="auth-subtitle">Welcome back! Please sign in to your account</p>
      </div>

      <form onSubmit={handleSubmit} className="auth-form">

        {/* USERNAME */}
        <div className="auth-field">
          <label className="auth-label">Username</label>
          <div className="auth-input-wrapper">
            <span className="auth-input-icon">👤</span>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              placeholder="Enter your username"
              autoComplete="username"
              required
              className="auth-input"
            />
          </div>
          <small className="auth-hint">Enter your username</small>
        </div>

        {/* PASSWORD */}
        <div className="auth-field">
          <label className="auth-label">Password</label>
          <div className="auth-input-wrapper">
            <span className="auth-input-icon">🔒</span>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
              autoComplete="current-password"
              required
              className="auth-input"
            />
          </div>
          <small className="auth-hint">Enter your password</small>
        </div>

        {error && (
          <div className="message error">
            <span style={{ fontSize: '1.25rem', flexShrink: 0 }}>⚠️</span>
            {error}
          </div>
        )}

        {success && (
          <div className="message success">
            <span style={{ fontSize: '1.25rem', flexShrink: 0 }}>✅</span>
            {success}
          </div>
        )}

        <button type="submit" className="auth-button" disabled={loading}>
          {loading && <span style={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            width: '1.5rem',
            height: '1.5rem',
            marginTop: '-0.75rem',
            marginLeft: '-0.75rem',
            border: '2px solid rgba(255,255,255,0.3)',
            borderRadius: '50%',
            borderTopColor: 'white',
            animation: 'spin 1s linear infinite'
          }}></span>}
          {loading ? 'Signing In...' : '🚀 Sign In'}
        </button>
      </form>

      <div className="auth-footer">
        <p className="auth-footer-text">Don't have an account? <a href="/register" className="auth-link">Sign up</a></p>
      </div>
    </div>
  );
}

export default Login;
