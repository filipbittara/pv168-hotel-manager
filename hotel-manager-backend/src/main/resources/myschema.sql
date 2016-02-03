CREATE TABLE guests (
  id                INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  fullName          VARCHAR(50),
  creditCardNumber  VARCHAR(50),
  passportNumber    VARCHAR(50),
  vipCustomer       BOOLEAN);

CREATE TABLE rooms (
  id                INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  number            INT,
  roomType          INT,
  capacity          INT,
  note              VARCHAR(50));

CREATE TABLE accommodations (
  id                INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  guestId           INT REFERENCES guests (id) ON DELETE CASCADE,
  roomId            INT REFERENCES rooms (id) ON DELETE CASCADE,
  checkIn           DATE,
  checkOut          DATE);