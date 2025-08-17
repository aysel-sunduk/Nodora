// WorkspacePopup.jsx (güncellenmiş)
import React, { useState } from 'react';
import '../components/css/WorkspacePopup.css';

export default function WorkspacePopup({ onClose, onSubmit }) {
    const [name, setName] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (name.trim()) {
            onSubmit(name);
        }
    };

    return (
        <div className="popup-overlay" onClick={onClose}>
            <div className="popup" onClick={e => e.stopPropagation()}>
                <h2>Yeni Çalışma Alanı</h2>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        className="popup-input"
                        placeholder="Çalışma alanı adı"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        autoFocus
                    />
                    <div className="popup-actions">
                        <button
                            type="button"
                            className="popup-button cancel"
                            onClick={onClose}
                        >
                            İptal
                        </button>
                        <button
                            type="submit"
                            className="popup-button submit"
                            disabled={!name.trim()}
                        >
                            Oluştur
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}