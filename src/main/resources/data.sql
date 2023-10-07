--Elimina los registros existentes para que no exista duplicados
TRUNCATE TABLE exchange;

-- Reinicia el contador de la secuencia
-- Lo hacemos ya que seguimos en un entorno de desarrollo
SELECT setval('exchange_exchange_id_seq', 1) ;


INSERT INTO exchange (money_name, money_iso, country)
VALUES
    ('DOLAR', 'USD', 'ESTADOS UNIDOS'),
    ('EURO', 'EUR', 'UNION EUROPEA'),
    ('YEN', 'JPY', 'JAPON'),
    ('RIYAL SAUDÍ', 'SAR', 'ARABIA SAUDITA'),
    ('DINAR ARGELINO', 'DZD', 'ARGELIA'),
    ('PESO', 'ARS', 'ARGENTINA'),
    ('DÓLAR', 'AUD', 'AUSTRALIA'),
    ('REAL', 'BRL', 'BRASIL'),
    ('DÓLAR', 'CAD', 'CANADA'),
    ('PESO', 'CLP', 'CHILE'),
    ('PESO', 'COP', 'COLOMBIA'),
    ('WON', 'KRW', 'COREA DEL SUR'),
    ('COLON COSTARRICENSE', 'CRC', 'COSTA RICA'),
    ('CORONA CHECA', 'CZK', 'REPUBLICA CHECA'),
    ('CORONA', 'DKK', 'DINAMARCA'),
    ('DÓLAR', 'USD', 'ECUADOR'),
    ('DIRHAM', 'AED', 'EMIRATOS ARABES'),
    ('PESO FILIPINO', 'PHP', 'FILIPINAS'),
    ('GOURDE', 'HTG', 'HAITÍ'),
    ('DÓLAR', 'HKD', 'HONG KONG'),
    ('RUPIA', 'INR', 'INDIA'),
    ('RUPIA INDONESIA', 'IDR', 'INDONESIA'),
    ('NUEVO SÉQUEL', 'ILS', 'ISRAEL'),
    ('RINGGIT MALAYO', 'MYR', 'MALASIA'),
    ('PESO', 'MXN', 'MEXICO'),
    ('CORONA', 'NOK', 'NORUEGA'),
    ('BALBOA', 'PAB', 'PANAMÁ'),
    ('GUARANI', 'PYG', 'PARAGUAY'),
    ('NUEVO SOL', 'PEN', 'PERU'),
    ('LIBRA', 'GBP', 'REINO UNIDO'),
    ('PESO DOMINICANO', 'DOP', 'REPÚBLICA DOMINICANA'),
    ('YUAN RENMINBI OFFSHORE', 'CNH', 'REP.POPULAR CHINA'),
    ('RUBLO RUSO', 'RUB', 'RUSIA'),
    ('DÓLAR', 'SGD', 'SINGAPUR'),
    ('RAND', 'ZAR', 'SUDÁFRICA'),
    ('CORONA', 'SEK', 'SUECIA'),
    ('FRANCO', 'CHF', 'SUIZA'),
    ('THAI BAHT', 'THB', 'TAILANDIA'),
    ('DÓLAR', 'TWD', 'TAIWAN'),
    ('DINAR TUNECINO', 'TND', 'TÚNEZ'),
    ('LIRA TURCA', 'TRY', 'TURQUÍA'),
    ('PESO', 'UYU', 'URUGUAY'),
    ('BOLIVAR DIGITAL', 'VES', 'VENEZUELA'),
    ('DONG', 'VND', 'VIETNAM'),
    ('Bs/UFV', 'UFV', 'UNIDAD DE FOMENTO DE VIVIENDA'),
    ('BOLIVIANO', 'BOL', 'BOLIVIA');
