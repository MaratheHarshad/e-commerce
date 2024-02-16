package com.bookcharm.app.service;

import com.bookcharm.app.exception.UnauthorizedAccessException;
import com.bookcharm.app.exception.UserNotFoundException;
import com.bookcharm.app.model.Category;
import com.bookcharm.app.model.Product;
import com.bookcharm.app.model.Seller;
import com.bookcharm.app.repository.ProductRepository;
import com.bookcharm.app.repository.SellerRepository;
import com.bookcharm.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.orElse(null);
    }

    @Override
    public Product addProduct(Product product, String jwtToken) {

        // validate the seller and add product in seller products
        // Add logic for product creation, validation, etc.



        Optional<Long> optionalSellerId = jwtUtil.verifySeller(jwtToken);

        if(optionalSellerId.isPresent()){
            Optional<Seller> optionalSeller = sellerRepository.findById(optionalSellerId.get());

            if(optionalSeller.isPresent()){

                Seller seller = optionalSeller.get();

                Product newProduct = new Product();

                newProduct.setProductName(product.getProductName());
                newProduct.setProductPrice(product.getProductPrice());
                newProduct.setProductDescription(product.getProductDescription());
                newProduct.setAuthor(product.getAuthor());
                newProduct.setCategory(new Category("BOOK"));
                newProduct.setIsbn(product.getIsbn());
                newProduct.setSeller(seller);
                newProduct.setStock(product.getStock());
                newProduct.setViewCount(0);
                newProduct.setProductImage("this is product image");

                seller.addProduct(newProduct);

                sellerRepository.save(seller);

                return newProduct;

            }else{

                throw new UserNotFoundException("Seller not found ");

            }
        }else{
            throw new UnauthorizedAccessException("Unauthorized access to add product");
        }


    }

    @Override
    public Product updateProduct(Long productId, Product updatedProduct) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            // Update the existing product with the new information
            existingProduct.setProductName(updatedProduct.getProductName());
            existingProduct.setProductPrice(updatedProduct.getProductPrice());
            existingProduct.setProductDescription(updatedProduct.getProductDescription());
            existingProduct.setProductImage(updatedProduct.getProductImage());
            existingProduct.setStock(updatedProduct.getStock());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setSeller(updatedProduct.getSeller());
            existingProduct.setViewCount(updatedProduct.getViewCount());
            existingProduct.setAuthor(updatedProduct.getAuthor());
            existingProduct.setIsbn(updatedProduct.getIsbn());

            // Save and return the updated product
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public boolean deleteProduct(Long productId, String jwtToken) {

        // validate the seller based on jwtToken
        // verify whether seller has product with ProductId
        // else throw UnAuthorization Exception

        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    // Add other ProductService methods if needed
}
