package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")//only logged in users
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;


    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            if (principal == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            int userId = user.getId();

            return shoppingCartDao.getByUserId(userId);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PostMapping("/products/{productId}")
    public ShoppingCart addProduct(@PathVariable int productId, Principal principal)
    {
        try
        {
            if (principal == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            int userId = user.getId();


            if (productDao.getById(productId) == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            shoppingCartDao.addProduct(userId, productId);

            return shoppingCartDao.getByUserId(userId);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PutMapping("/products/{productId}")
    public ShoppingCart updateProduct(@PathVariable int productId,
                                      @RequestBody ShoppingCartItem item,
                                      Principal principal)
    {
        try
        {
            if (principal == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            int userId = user.getId();

            if (item == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required.");

            if (item.getQuantity() < 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be 0 or more.");

            shoppingCartDao.updateProduct(userId, productId, item.getQuantity());
            return shoppingCartDao.getByUserId(userId);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // DELETE /cart   (clears current user's cart)
    @DeleteMapping
    public ShoppingCart clearCart(Principal principal)
    {
        try
        {
            if (principal == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            int userId = user.getId();

            shoppingCartDao.clearCart(userId);

            return shoppingCartDao.getByUserId(userId);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

}
