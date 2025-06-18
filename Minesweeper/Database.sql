CREATE DATABASE MINESWEEPER

USE MINESWEEPER

CREATE TABLE Players (
    PlayerID INT IDENTITY PRIMARY KEY,
    PlayerName NVARCHAR(50) NOT NULL UNIQUE,
);

CREATE TABLE Scores (
    PlayerID INT FOREIGN KEY REFERENCES Players(PlayerID),
    TimeTakenInSeconds INT NOT NULL,
);

CREATE VIEW BangXepHang AS
SELECT 
    P.PlayerName,
    MIN(S.TimeTakenInSeconds) AS BestTime
FROM Scores S
JOIN Players P ON S.PlayerID = P.PlayerID
GROUP BY P.PlayerName

-- Chèn dữ liệu vào bảng Players
INSERT INTO Players (PlayerName) VALUES 
(N'Nguyen Van A'),
(N'Tran Thi B'),
(N'Le Van C'),
(N'Pham Thi D'),
(N'Hoang Van E'),
(N'Nguyen Thi F'),
(N'Do Van G'),
(N'Bui Thi H'),
(N'Tran Van I'),
(N'Nguyen Van J');

-- Chèn dữ liệu vào bảng Scores
-- Giả sử mỗi người chơi có 2 kết quả khác nhau
INSERT INTO Scores (PlayerID, TimeTakenInSeconds) VALUES 
(1, 120),
(1, 115),
(2, 130),
(2, 125),
(3, 140),
(3, 138),
(4, 110),
(4, 112),
(5, 150),
(5, 145),
(6, 100),
(6, 105),
(7, 135),
(7, 133),
(8, 160),
(8, 155),
(9, 95),
(9, 98),
(10, 170),
(10, 165);

SELECT * FROM BangXepHang ORDER BY BestTime ASC;

