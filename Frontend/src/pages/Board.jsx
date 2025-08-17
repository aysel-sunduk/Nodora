import React, { useState, useContext } from 'react';
import { FiPlus } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import BoardPopup from './BoardPopup.jsx';
import { ThemeContext } from '../App';
import '../components/css/Board.css';

export default function Board({ boards, onCreateBoardSubmit, workspaceId }) {
    const [showBoardPopup, setShowBoardPopup] = useState(false);
    const navigate = useNavigate();
    const { theme } = useContext(ThemeContext);

    const handleBoardPopupSubmit = async (boardTitle, bgColor) => {
        await onCreateBoardSubmit({
            workspaceId,
            title: boardTitle,
            bgColor: bgColor || '#ADD8E6',
        });
        setShowBoardPopup(false);
    };

    const handleBoardClick = (boardId) => {
        if (!boardId) {
            console.error("Pano ID is missing for navigation.");
            alert("Pano detayına gitmek için pano ID'si eksik.");
            return;
        }
        navigate(`/board/${boardId}`);
    };

    return (
        <div className="board-list" data-theme={theme}>
            <div className="boards-grid">
                {boards && boards.length > 0 ? (
                    boards.map((board) => (
                        <div
                            key={board.id}
                            className="board-card"
                            onClick={() => handleBoardClick(board.id)}

                        >
                            <h3>{board.title}</h3>
                        </div>
                    ))
                ) : (
                    <p className="no-boards-message">Bu çalışma alanına ait pano bulunamadı.</p>
                )}
                <div
                    className="board-card add-board-card"
                    onClick={() => setShowBoardPopup(true)}
                >
                    <FiPlus size={24} />
                    <span>Pano Ekle</span>
                </div>
            </div>

            {showBoardPopup && (
                <BoardPopup
                    onClose={() => setShowBoardPopup(false)}
                    onSubmit={handleBoardPopupSubmit}
                />
            )}
        </div>
    );
}