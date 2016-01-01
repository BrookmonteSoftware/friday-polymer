/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.mapdb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.brookmonte.friday.FridayPolymer.manager.properties.PropertiesManager;

/**
 * @author Pete
 * 
 */
public class MBTilesManager
{
    private static org.apache.log4j.Logger log = Logger.getLogger(MBTilesManager.class);

    @Autowired
    private PropertiesManager propertiesManager;

    private Connection connection = null;

    /**
     * cleanup
     * 
     * Closes the connection to the Sqlite map tiles database.
     * This method is called by Spring as the "destroy-method"
     * via the bean declaration.
     * 
     * @throws SQLException
     */
    public synchronized void cleanup() throws SQLException
    {
        if (connection != null)
        {
            connection.close();
            connection = null;
        }        
    }
    
    /**
     * getConnection
     * 
     * Makes a connection to the Sqlite database. This class assumes that
     * it is running as a singleton, so the connection is cached. The connection
     * is closed when the object is destroyed by Spring, The Spring
     * applicationContext.xml file instantiates this object as a singleton, and
     * declares a "destroy-method" on it, which calls the cleanup() method
     * to close the connection.
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private synchronized void getConnection() throws ClassNotFoundException, SQLException
    {
        Class.forName("org.sqlite.JDBC");
        String mbtilesPath = propertiesManager.getDbProperties().getProperty("mapdb.path");

        // create a database connection
        connection = DriverManager.getConnection("jdbc:sqlite:" + mbtilesPath);
    }
    
    /**
     * getTile
     * 
     * Retrieve a tile from the MBTiles-format Sqlite database, based on the
     * x and y coordinates and zoom level. The Leaflet javascript library
     * makes the necessary conversion between lat/long to x, y. Other mapping
     * javascript libraries will make the same conversion.
     * 
     * @param x
     * @param y
     * @param z
     * @return
     * @throws ClassNotFoundException
     */
    public synchronized byte[] getTile(String x, String y, String z) throws ClassNotFoundException
    {
        try
        {
            if (connection == null)
            {
                getConnection();
            }

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
           
            String qs = "select tile_data from tiles where tile_column=" + x + " and tile_row=" + y + " and zoom_level=" + z;

            //System.out.println("QRY: " + qs);
            
            ResultSet rs = statement.executeQuery(qs);
            
            if (rs.next())
            {
                // can this byte array be returned directly?
                byte[] tileBytes = rs.getBytes("tile_data");
    
                // convert byte array to BufferedImage
                InputStream in = new ByteArrayInputStream(tileBytes);
                BufferedImage bImageFromConvert = ImageIO.read(in);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
                // convert the byte array to a PNG file - maybe this step isn't necessary?
                ImageIO.write(bImageFromConvert, "png", baos);
    
                // convert the PNG file to a byte array for streaming back to the browser
                return baos.toByteArray();
            }
        }
        catch (SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * listTiles
     * 
     * Lists all of the tiles at zoom level 9 in the Sqllist map tiles database.
     * Used for debugging.
     * 
     * @throws ClassNotFoundException
     */
    public synchronized void listTiles() throws ClassNotFoundException
    {
        try
        {
            if (connection == null)
            {
                getConnection();
            }
            
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.

            String qs = "select tile_column, tile_row, zoom_level from tiles where zoom_level=9";

            ResultSet rs = statement.executeQuery(qs);
            
            while (rs.next())
            {
                Integer tileColumn = rs.getInt("tile_column");
                Integer tileRow = rs.getInt("tile_row");
                Integer zoomLevel = rs.getInt("zoom_level");

                //System.out.println(tileColumn + "\t" + tileRow + "\t" + zoomLevel);
                log.info(tileColumn + "\t" + tileRow + "\t" + zoomLevel);
            }

        }
        catch (SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
       
    }
}
