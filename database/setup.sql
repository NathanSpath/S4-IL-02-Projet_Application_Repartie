--cleanup des tables
DROP TABLE RMI_RESERVATION CASCADE CONSTRAINTS;
DROP TABLE RMI_CLIENT CASCADE CONSTRAINTS;
DROP TABLE RMI_RESTAURANTS CASCADE CONSTRAINTS;

--creation des tables
CREATE TABLE RMI_RESTAURANTS(
                                id RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,
                                nom VARCHAR2(255),
                                adresse VARCHAR2(255),
                                coord VARCHAR2(255)
);

CREATE TABLE RMI_CLIENT(
                           id RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,
                           nom VARCHAR2(255),
                           prenom VARCHAR2(255),
                           numtel VARCHAR2(255)
);

CREATE TABLE RMI_RESERVATION(
                                idCli RAW(16) NOT NULL,
                                idRes RAW(16) NOT NULL,
                                nbConvives NUMBER,
                                CONSTRAINT FK_RMI_idCLI FOREIGN KEY (idCli) REFERENCES RMI_CLIENT(id),
                                CONSTRAINT FK_RMI_idRES FOREIGN KEY (idRes) REFERENCES RMI_RESTAURANTS(id),
                                CONSTRAINT PK_RMI_RESERVATION PRIMARY KEY (idCli, idRes)
);

--insertion des données
INSERT INTO RMI_RESTAURANTS (id, nom, adresse, coord) VALUES (SYS_GUID(), 'Le Grenier à Sel', '28 Place de la Carrière, 54000 Nancy', '48.696614, 6.182247');
INSERT INTO RMI_RESTAURANTS (id, nom, adresse, coord) VALUES (SYS_GUID(), 'Excelsior', '50 Rue Henri Poincaré, 54000 Nancy', '48.690833, 6.176389');
INSERT INTO RMI_RESTAURANTS (id, nom, adresse, coord) VALUES (SYS_GUID(), 'Le Bouche à Oreille', '42 Rue des Carmes, 54000 Nancy', '48.691472, 6.181822');
INSERT INTO RMI_RESTAURANTS (id, nom, adresse, coord) VALUES (SYS_GUID(), 'La Table du Bon Roi Stanislas', '7 Rue Gustave Simon, 54000 Nancy', '48.693631, 6.181315');
INSERT INTO RMI_RESTAURANTS (id, nom, adresse, coord) VALUES (SYS_GUID(), 'Les Frères Marchand', '99 Grande Rue, 54000 Nancy', '48.695392, 6.179914');
INSERT INTO RMI_RESTAURANTS (id, nom, adresse, coord) VALUES (SYS_GUID(), 'RAYA', '1 Bd de l''Insurrection du Ghetto de Varsovie, 54000 Nancy','48.685558, 6.181652');
INSERT INTO RMI_RESTAURANTS (id, nom, adresse, coord) VALUES (SYS_GUID(), 'Chicken Street','16 Av. du Général Leclerc, 54000 Nancy','48.684414, 6.186244');

--commit
COMMIT;

--ajout des collaborateurs
GRANT SELECT, INSERT, UPDATE ON RMI_RESTAURANTS TO SPATH2U, E65120U, E54916U, E85474U;
GRANT SELECT, INSERT, UPDATE ON RMI_CLIENT TO SPATH2U, E65120U, E54916U, E85474U;
GRANT SELECT, INSERT, UPDATE ON RMI_RESERVATION TO SPATH2U, E65120U, E54916U, E85474U;



