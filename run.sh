BASEDIR=$(dirname "$0")

config=$1
echo "$config" 
. $config
cd $BASEDIR/TrueFilm
mvn clean install

PGPASSWORD=$password  psql -U $username -d $database -c "INSERT INTO moviesdetails select ranked_highest_ratio.* from ( select a.title,a.budget,a.year,a.revenue,b.rating,a.ratio,a.production_companies,c.wikipedia_page , ROW_NUMBER()  OVER (PARTITION BY a.title ORDER BY a.ratio DESC) rn from moviesmetadata a left join moviesratings b on a.id = b.movieId left join movieslist c on a.title = c.title ) ranked_highest_ratio where rn = 1  order by ranked_highest_ratio.ratio desc limit 1000;"

#PGPASSWORD=$password  psql -U $username -d $database -c "DROP TABLE IF EXISTS moviesdetails; CREATE TABLE moviesdetails as select a.title,a.budget,a.year,a.revenue,b.rating,a.ratio,a.production_companies,c.wikipedia_page from moviesmetadata a left join moviesratings b on a.id = b.movieId left join movieslist c on a.title = c.title order by a.ratio desc limit 1000;"
