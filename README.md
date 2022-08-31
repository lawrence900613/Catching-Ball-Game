# Introduction
In this project, our group has designed a basic multiplayer action game which we have titled as Ball Game. The game rules are simple, every player will be assigned a color when entering the lobby and will be playing in a free for all mode. A ball will be bouncing around the screen at certain speeds, and at a fixed size. The player’s goal is to accurately click on the ball and keep it in your possession and control as long as possible; however, a player may not hold the ball for more than 1 second, where the game will force the player to release the ball. While the ball is under control of another player, no other player will be able to steal possession of the ball. In success of attaining control of the ball (clicking and holding the game ball) the player will receive a point. The game ends after a 1 minute timer and the player with the most points wins.

# Game Initialisation
	The Ball Game (“The game”) is started by running com.BallGame.GameWindowServer, where the process will begin to listen for up to 10 players asynchronously, whose thread callable function can be found in com.BallGame.net.SocketListen. The clients can then connect to the server by running com.BallGame.GameWindowClient. 
Note that the server must start prior to clients connecting, lest connection failure occurs.

# Game Session
	Once the game session starts, the server will asynchronously listen and broadcast the current game state to all clients using the acquired connection sockets during initialisation. 
To achieve asynchronous in-order message reading, each connected socket is listened to using an instance of the ISListen class (com.BallGame.net.ISListen). The Handler class (com.BallGame.net.Handler) then consolidates all read messages into a queue, of which the server-side game logic can then handle individually.

This application was developed by Jun Pin Foo, Eric Chen, Shao-en Hung, Nhi Mai-Do, and Michael Chong Michael Chong. (Data Communications and Networking)
