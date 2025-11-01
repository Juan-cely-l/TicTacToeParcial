const { useState, useEffect, useRef } = React;

// ID único del jugador
const PLAYER_ID = 'player-' + Math.random().toString(36).substr(2, 9);
const GAME_ID = 'room 1';

function Square({ value, onSquareClick, disabled }) {
    return (
        <button
            className="square"
            onClick={onSquareClick}
            disabled={disabled}
        >
            {value}
        </button>
    );
}

function Board({ squares, onSquareClick, currentTurn, myPlayer, gameStatus }) {
    const isMyTurn = currentTurn === myPlayer;
    const gameOver = gameStatus && (gameStatus.startsWith('WINNER') || gameStatus === 'DRAW');

    let status;
    if (gameStatus === 'WAITING') {
        status = 'Esperando al otro jugador...';
    } else if (gameStatus && gameStatus.startsWith('WINNER')) {
        const winner = gameStatus.split(':')[1];
        status = `¡Ganador: ${winner}!`;
    } else if (gameStatus === 'DRAW') {
        status = '¡Empate!';
    } else if (gameStatus === 'OCCUPIED') {
        status = 'Casilla ocupada, intenta otra';
    } else if (gameStatus === 'NOT_YOUR_TURN') {
        status = 'No es tu turno';
    } else {
        status = `Turno de: ${currentTurn} ${isMyTurn ? '(Tu turno)' : ''}`;
    }

    function handleClick(i) {
        if (gameOver || !isMyTurn || squares[i]) {
            return;
        }
        onSquareClick(i);
    }

    return (
        <>
            <div className="status">{status}</div>
            <div className="player-info">
                Eres el jugador: <strong>{myPlayer || 'Conectando...'}</strong>
            </div>
            <div className="board-row">
                <Square value={squares[0]} onSquareClick={() => handleClick(0)} disabled={gameOver || !isMyTurn} />
                <Square value={squares[1]} onSquareClick={() => handleClick(1)} disabled={gameOver || !isMyTurn} />
                <Square value={squares[2]} onSquareClick={() => handleClick(2)} disabled={gameOver || !isMyTurn} />
            </div>
            <div className="board-row">
                <Square value={squares[3]} onSquareClick={() => handleClick(3)} disabled={gameOver || !isMyTurn} />
                <Square value={squares[4]} onSquareClick={() => handleClick(4)} disabled={gameOver || !isMyTurn} />
                <Square value={squares[5]} onSquareClick={() => handleClick(5)} disabled={gameOver || !isMyTurn} />
            </div>
            <div className="board-row">
                <Square value={squares[6]} onSquareClick={() => handleClick(6)} disabled={gameOver || !isMyTurn} />
                <Square value={squares[7]} onSquareClick={() => handleClick(7)} disabled={gameOver || !isMyTurn} />
                <Square value={squares[8]} onSquareClick={() => handleClick(8)} disabled={gameOver || !isMyTurn} />
            </div>
        </>
    );
}

function Game() {
    const [squares, setSquares] = useState(Array(9).fill(null));
    const [currentTurn, setCurrentTurn] = useState('X');
    const [myPlayer, setMyPlayer] = useState(null);
    const [gameStatus, setGameStatus] = useState('WAITING');
    const [connected, setConnected] = useState(false);
    const stompClientRef = useRef(null);
    const myPlayerRef = useRef(null);

    useEffect(() => {
        // Crear conexión WebSocket
        const socket = new SockJS('http://localhost:8080/ws-tictactoe');
        const stompClient = window.StompJs.Stomp.over(socket);

        stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            setConnected(true);

            // Suscribirse al topic del juego
            stompClient.subscribe('/topic/game/' + GAME_ID, function(message) {
                const gameState = JSON.parse(message.body);
                console.log('Received game state:', gameState);
                console.log('My PLAYER_ID:', PLAYER_ID);
                console.log('GameState playerX:', gameState.playerX);
                console.log('GameState playerO:', gameState.playerO);

                // Actualizar el estado del juego
                setSquares(gameState.board || Array(9).fill(null));
                setCurrentTurn(gameState.currentTurn || 'X');
                setGameStatus(gameState.status);

                // Determinar qué jugador soy yo basándome en la respuesta del servidor
                if (!myPlayerRef.current) {
                    if (gameState.playerX === PLAYER_ID) {
                        myPlayerRef.current = 'X';
                        setMyPlayer('X');
                        console.log(' I am player X');
                    } else if (gameState.playerO === PLAYER_ID) {
                        myPlayerRef.current = 'O';
                        setMyPlayer('O');
                        console.log(' I am player O');
                    }
                }
            });

            console.log('Joining game with PLAYER_ID:', PLAYER_ID);
            stompClient.send('/app/game.join/' + GAME_ID, {}, JSON.stringify({
                playerId: PLAYER_ID
            }));
        }, function(error) {
            console.error('Error connecting:', error);
            setConnected(false);
        });

        stompClientRef.current = stompClient;

        return () => {
            if (stompClient.connected) {
                stompClient.disconnect();
            }
        };
    }, []);

    function handlePlay(index) {
        if (!connected || !stompClientRef.current) {
            console.error('Not connected to server');
            return;
        }

        if (!myPlayer) {
            console.error('Player not assigned yet');
            return;
        }

        const move = {
            index: index,
            player: myPlayer,
            gameId: GAME_ID
        };

        console.log('Sending move:', move);

        stompClientRef.current.send('/app/game.play/' + GAME_ID, {}, JSON.stringify(move));
    }

    function handleReset() {
        if (!connected || !stompClientRef.current) {
            return;
        }
        stompClientRef.current.send('/app/game.reset/' + GAME_ID, {}, JSON.stringify({}));
    }

    return (
        <div className="game">
            <div className="game-board">
                <h1>Tic Tac Toe - Multijugador</h1>
                <Board
                    squares={squares}
                    onSquareClick={handlePlay}
                    currentTurn={currentTurn}
                    myPlayer={myPlayer}
                    gameStatus={gameStatus}
                />
                <div className="game-controls">
                    <button
                        className="reset-button"
                        onClick={handleReset}
                        disabled={!connected}
                    >
                        Reiniciar Juego
                    </button>
                    <div className="connection-status">
                        {connected ? 'Conectado' : 'Desconectado'}
                    </div>
                </div>
            </div>
            <div className="game-info">
                <h3>Instrucciones:</h3>
                <ol>
                    <li>Abre esta página en dos pestañas diferentes</li>
                    <li>El primer jugador será X, el segundo será O</li>
                    <li>Los jugadores se turnan automáticamente</li>
                    <li>Las jugadas se sincronizan en tiempo real</li>
                </ol>
                <div className="game-id">
                    Sala: <strong>{GAME_ID}</strong>
                </div>
            </div>
        </div>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Game />);
